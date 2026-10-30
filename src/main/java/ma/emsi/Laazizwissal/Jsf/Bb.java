package ma.emsi.Laazizwissal.Jsf;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.model.SelectItem;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import ma.emsi.Laazizwissal.service.JsonAdapterPourGemini;
import ma.emsi.Laazizwissal.service.LlmInteraction;

@Named
@ViewScoped
public class Bb implements Serializable {

    private String roleSysteme;
    private boolean roleSystemeChangeable = true;
    private List<SelectItem> listeRolesSysteme;
    private String question;
    private String reponse;
    private StringBuilder conversation = new StringBuilder();
    private boolean debug = false;
    private String texteRequeteJson;
    private String texteReponseJson;

    @Inject
    private JsonAdapterPourGemini jsonAdapter;

    @Inject
    private FacesContext facesContext;

    public String getRoleSysteme() {
        return this.roleSysteme;
    }

    public void setRoleSysteme(String roleSysteme) {
        this.roleSysteme = roleSysteme;
    }

    public boolean isRoleSystemeChangeable() {
        return this.roleSystemeChangeable;
    }

    public String getQuestion() {
        return this.question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getReponse() {
        return this.reponse;
    }

    public void setReponse(String reponse) {
        this.reponse = reponse;
    }

    public String getConversation() {
        return conversation.toString();
    }

    public void setConversation(String conversation) {
        this.conversation = new StringBuilder(conversation);
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public String getTexteRequeteJson() {
        return texteRequeteJson;
    }

    public void setTexteRequeteJson(String texteRequeteJson) {
        this.texteRequeteJson = texteRequeteJson;
    }

    public String getTexteReponseJson() {
        return texteReponseJson;
    }

    public void setTexteReponseJson(String texteReponseJson) {
        this.texteReponseJson = texteReponseJson;
    }

    public String envoyer() {

        if (this.question == null || this.question.isBlank()) {
            FacesMessage message = new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Texte question vide",
                    "Il manque le texte de la question"
            );

            facesContext.addMessage(null, message);
            return null;
        }

        if (this.conversation.isEmpty()) {
            this.roleSystemeChangeable = false;
            this.jsonAdapter.setSystemRole(this.roleSysteme);
        }

        try {
            LlmInteraction interaction = jsonAdapter.envoyerRequete(question);

            this.reponse = interaction.reponseExtraite();
            this.texteRequeteJson = interaction.questionJson();
            this.texteReponseJson = interaction.reponseJson();

        } catch (Exception e) {
            this.reponse = "Erreur : " + e.getMessage();

            FacesMessage message = new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Problème de connexion avec l'API du LLM",
                    "Problème de connexion avec l'API du LLM"
                            + e.getMessage()
                            + "\n"
                            + Arrays.toString(e.getStackTrace())
            );

            facesContext.addMessage(null, message);
        }

        afficherConversation();

        return null;
    }

    public String nouveauChat() {
        return "index";
    }

    private void afficherConversation() {
        this.conversation
                .append("== User:\n")
                .append(question)
                .append("\n== Serveur:\n")
                .append(reponse)
                .append("\n");
    }

    public List<SelectItem> getRolesSysteme() {

        if (this.listeRolesSysteme == null) {
            this.listeRolesSysteme = new ArrayList<>();

            String role = """
                    You are a helpful assistant. You help the user to find the information they need.
                    If the user type a question, you answer it.
                    """;

            this.listeRolesSysteme.add(
                    new SelectItem(role, "Assistant")
            );

            role = """
                    You are an interpreter. You translate from English to French and from French to English.
                    If the user type a French text, you translate it into English.
                    If the user type an English text, you translate it into French.
                    If the text contains only one to three words, give some examples of usage of these words in English.
                    """;

            this.listeRolesSysteme.add(
                    new SelectItem(role, "Traducteur Anglais-Français")
            );

            role = """
                    Your are a travel guide. If the user type the name of a country or of a town,
                    you tell them what are the main places to visit in the country or the town
                    are you tell them the average price of a meal.
                    """;

            this.listeRolesSysteme.add(
                    new SelectItem(role, "Guide touristique")
            );

            role = """
                    You are a technical interview coach specialized in software engineering.
                    You help computer science students prepare for job interviews.

                    When the user asks about a technical topic:
                    - Explain the concept clearly and briefly.
                    - Give one practical example.
                    - Ask one relevant technical interview question.
                    - Wait for the user's answer before evaluating it.

                    When the user answers your interview question:
                    - Evaluate the answer objectively.
                    - Explain what is correct and what should be improved.
                    - Provide a model answer.
                    - Ask a new interview question.

                    Always respond in French using a professional and encouraging tone.
                    """;

            this.listeRolesSysteme.add(
                    new SelectItem(role, "Coach d'entretien informatique")
            );
        }

        return this.listeRolesSysteme;
    }
}
