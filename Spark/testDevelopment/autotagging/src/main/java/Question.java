public class Question {
    private String questionId;
    private String questionTitle;
    private String questionBody;
    private String questionTags;
    private String questionTime;
    private String ownerId;

    public Question() {

    }

    public Question(String questionId, String questionTitle, String questionTime, String ownerId, String questionTags) {
        this.questionId = questionId;
        this.questionTitle = questionTitle;
        this.questionTime = questionTime;
        this.ownerId = ownerId;
    }

    public String getQuestionId() {
        return questionId;
    }

    public String getQuestionTitle() {
        return questionTitle;
    }

    public String ownerId() {
        return ownerId;
    }

    public String getQuestionTags() {
        return questionTags;
    }

    public String setQuestionId(String questionId) {
        this.questionId = questionId;
        return questionId;
    }

    public String setQuestionTags(String questionTags) {
        this.questionTags = questionTags;
        return questionTags;
    }

    public String setQuestionTitle(String questionTitle) {
        this.questionTitle = questionTitle;
        return questionTitle;
    }

}
