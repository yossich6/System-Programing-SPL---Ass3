package Reactor.protocol;

public class questions {
	private String questionText;
	private String realAnswer;
	
	public questions(String questionText, String realAnswer) {
		this.questionText = questionText;
		this.realAnswer = realAnswer;
	}

	public String getQuestionText() {
		return questionText;
	}

	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}

	public String getRealAnswer() {
		return realAnswer;
	}

	public void setRealAnswer(String realAnswer) {
		this.realAnswer = realAnswer;
	}

	

}
