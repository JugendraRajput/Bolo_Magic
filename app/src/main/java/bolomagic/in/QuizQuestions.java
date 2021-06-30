package bolomagic.in;

public class QuizQuestions {
    String questionID;
    String question;
    String optionA;
    String optionB;
    String optionC;
    String optionD;
    String answer;

    public QuizQuestions(String questionID, String question, String optionA, String optionB, String optionC, String optionD, String answer) {
        this.questionID = questionID;
        this.question = question;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.answer = answer;
    }

    public String getQuestionID() {
        return questionID;
    }

    public String getQuestion() {
        return question;
    }

    public String getOptionA() {
        return optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
