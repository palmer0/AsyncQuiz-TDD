package es.ulpgc.eite.da.quiz.question;

public class QuestionViewModel {

    // put the view state here
    public String questionText;

    // Tiempo restante del contador
    public int remainingTime = 0;
    // Última respuesta correcta/incorrecta
    public String resultText;
    //public Boolean lastAnswerCorrect = null;

    public boolean falseButton = true;
    public boolean trueButton = true;
    public boolean cheatButton = true;
    public boolean nextButton = false;

}

