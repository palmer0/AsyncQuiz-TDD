package es.ulpgc.eite.da.quiz.question;

import android.util.Log;

import java.lang.ref.WeakReference;

import es.ulpgc.eite.da.quiz.app.AppMediator;
import es.ulpgc.eite.da.quiz.app.CheatToQuestionState;
import es.ulpgc.eite.da.quiz.app.QuestionToCheatState;


public class QuestionPresenter implements QuestionContract.Presenter {

    public static String TAG = "Quiz.QuestionPresenter";

    private WeakReference<QuestionContract.View> view;
    private QuestionState state;
    private QuestionContract.Model model;
    private AppMediator mediator;

    //private int savedTime;  // Tiempo guardado en Mediator
    //private Boolean savedAnswer;  // Respuesta guardada en Mediator


    public QuestionPresenter(AppMediator mediator) {
        this.mediator = mediator;
    }

    @Override
    public void onCreateCalled() {
        Log.e(TAG, "onCreateCalled");

        // init the screen state
        state = new QuestionState();
        state.resultText = model.getEmptyResultText();
        //state.lastAnswerCorrect = null;
        state.questionText = model.getCurrentQuestion();
    }


//    @Override
//    public void onRecreateCalled() {
//        Log.e(TAG, "onRecreateCalled");
//
//        // restore the screen state
//        state = mediator.getQuestionState();
//
//        // update the model
//        model.setCurrentIndex(state.quizIndex);
//
//    }


    @Override
    public void onRecreateCalled() {
        Log.e(TAG, "onRecreateCalled");

        state = mediator.getQuestionState();
        model.setCurrentIndex(state.quizIndex);
        
        //savedTime = mediator.getRemainingTime();
        int savedTime = state.remainingTime;

        /*
        //savedAnswer = mediator.getLastAnswerCorrect();
        Boolean savedAnswer = state.lastAnswerCorrect;

        if (savedTime > 0) {
            resumeCountdown(savedTime);
        } else if (savedAnswer != null) {
            updateResultText(savedAnswer);
        }
        */

        if (savedTime > 0) {
            resumeCountdown(savedTime);
        } else if (!state.nextButton) {
            updateResultText(state.resultText);
        }
    }

    @Override
    public void onResumeCalled() {
        Log.e(TAG, "onResumeCalled");

        // get the saved state from the next screen
        CheatToQuestionState savedState = mediator.getCheatToQuestionState();
        if (savedState != null) {

            // update the current state
            if (savedState.cheated) {
                nextButtonClicked();
                return;
            }
        }

        // refresh the display with updated state
        view.get().displayQuestionData(state);

    }

    @Override
    public void onPauseCalled() {
        Log.e(TAG, "onPauseCalled");

        // save the current state
        mediator.setQuestionState(state);
    }

    @Override
    public void onDestroyCalled() {
        Log.e(TAG, "onDestroyCalled");

        // Reset current state
        //mediator.resetCheatState();
    }

    private void resumeCountdown(int timeLeft) {
        model.processAnswerWithCountdown(true,
            new QuestionContract.Model.OnAnswerProcessedWithCountdownListener() {

            @Override
            public void onTimeUpdate(int secsRemaining) {
                state.resultText = "" + secsRemaining;
                //mediator.setRemainingTime(secsRemaining);
                state.remainingTime=secsRemaining;
                view.get().displayQuestionData(state);
            }

                @Override
                public void onAnswerProcessed(String resultText) {
                    //mediator.setLastAnswerCorrect(isCorrect);
                    // Restablecer contador después de respuesta
                    state.remainingTime= 0;
                    //mediator.setRemainingTime(0);
                    updateResultText(resultText);
                }

            /*
            @Override
            public void onAnswerProcessed(boolean isCorrect) {
                //mediator.setLastAnswerCorrect(isCorrect);
                // Restablecer contador después de respuesta
                state.remainingTime= 0;
                //mediator.setRemainingTime(0);
                updateResultText(isCorrect);
            }
            */
        }, timeLeft);
    }

    /*
    private void updateQuestionData(boolean userAnswer) {

        // update the current state

        boolean currentAnswer = model.getCurrentAnswer();

        if (currentAnswer == userAnswer) {
            state.resultText = model.getCorrectResultText();

        } else {
            state.resultText = model.getIncorrectResultText();
        }

        state.falseButton = false;
        state.trueButton = false;
        state.cheatButton = false;

        if (model.isLastQuestion()) {
            state.nextButton = false;

        } else {
            state.nextButton = true;
        }

        // refresh the display with updated state
        view.get().displayQuestionData(state);
    }
    */

//    private void updateResultText(boolean isCorrect) {
//        if (isCorrect) {
//            state.resultText = model.getCorrectResultText();
//            //state.lastAnswerCorrect = true;
//        } else {
//            state.resultText = model.getIncorrectResultText();
//            //state.lastAnswerCorrect = false;
//        }
//        state.nextButton = !model.isLastQuestion();
//        view.get().displayQuestionData(state);
//    }


    private void updateResultText(String resultText) {
        state.resultText = resultText;
        state.nextButton = !model.isLastQuestion();
        view.get().displayQuestionData(state);
    }

    private void processAnswerWithCountdown(boolean userAnswer) {
        state.falseButton = false;
        state.trueButton = false;
        state.cheatButton = false;

        view.get().displayQuestionData(state);

        // Obtener tiempo restante desde Mediator
        int resumeTime = state.remainingTime;
        //int resumeTime = mediator.getRemainingTime();

        model.processAnswerWithCountdown(userAnswer,
            new QuestionContract.Model.OnAnswerProcessedWithCountdownListener() {

                @Override
                public void onTimeUpdate(int secsRemaining) {
                    state.resultText = "" + secsRemaining;
                    //mediator.setRemainingTime(secsRemaining);
                    state.remainingTime = secsRemaining;
                    view.get().displayQuestionData(state);
                }

                @Override
                public void onAnswerProcessed(String resultText) {
                    //mediator.setRemainingTime(0);
                    state.remainingTime = 0;
                    //mediator.setLastAnswerCorrect(isCorrect);
                    updateResultText(resultText);
                }

                /*
                @Override
                public void onAnswerProcessed(boolean isCorrect) {
                    //mediator.setRemainingTime(0);
                    state.remainingTime = 0;
                    //mediator.setLastAnswerCorrect(isCorrect);
                    updateResultText(isCorrect);
                }
                */
            }, resumeTime);
    }

//    private void processAnswerWithCountdown(boolean userAnswer) {
//
//        // Deshabilitar botones mientras se espera la respuesta
//        state.falseButton = false;
//        state.trueButton = false;
//        state.cheatButton = false;
//        view.get().displayQuestionData(state);
//
//        // Llamar al modelo y pasar listener para recibir respuesta después del retardo
//        model.processAnswerWithCountdown(userAnswer,
//            new QuestionContract.Model.OnAnswerProcessedWithCountdownListener() {
//
//            @Override
//            public void onTimeUpdate(int secsRemaining) {
//                state.resultText = "" + secsRemaining;
//                view.get().displayQuestionData(state);
//            }
//
//            @Override
//            public void onAnswerProcessed(boolean isCorrect) {
//
//                // Una vez que modelo procesa respuesta, actualizamos vista
//                if (isCorrect) {
//                    state.resultText = model.getCorrectResultText();
//                } else {
//                    state.resultText = model.getIncorrectResultText();
//                }
//
//                state.nextButton = !model.isLastQuestion();
//
//                // Actualizar vista con respuesta
//                view.get().displayQuestionData(state);
//            }
//        });
//    }

    /*
    private void processAnswer(boolean userAnswer) {

        // Deshabilitar botones mientras se espera respuesta
        state.falseButton = false;
        state.trueButton = false;
        state.cheatButton = false;
        view.get().displayQuestionData(state);

        // Llamar al modelo y pasar listener para recibir respuesta después del retardo
        model.processAnswer(userAnswer, isCorrect -> {

            // Una vez que modelo procesa respuesta, actualizamos vista
            if (isCorrect) {
                state.resultText = model.getCorrectResultText();
            } else {
                state.resultText = model.getIncorrectResultText();
            }

            state.nextButton = !model.isLastQuestion();

            // Actualizar vista con respuesta
            view.get().displayQuestionData(state);
        });
    }
    */

    @Override
    public void trueButtonClicked() {
        Log.e(TAG, "trueButtonClicked");

        //updateQuestionData(true);
        processAnswerWithCountdown(true);
    }

    @Override
    public void falseButtonClicked() {
        Log.e(TAG, "falseButtonClicked");

        //updateQuestionData(false);
        processAnswerWithCountdown(false);
    }

    @Override
    public void cheatButtonClicked() {
        Log.e(TAG, "cheatButtonClicked");

        // save the state to next screen
        boolean answer = model.getCurrentAnswer();
        QuestionToCheatState newState = new QuestionToCheatState(answer);
        mediator.setQuestionToCheatState(newState);

        // navigate to next screen
        view.get().navigateToCheatScreen();
    }

    @Override
    public void nextButtonClicked() {
        Log.e(TAG, "nextButtonClicked");

        // update the current state

        model.incrQuizIndex();
        state.quizIndex  = model.getCurrentIndex();

        state.questionText = model.getCurrentQuestion();
        state.resultText = model.getEmptyResultText();
        //state.lastAnswerCorrect = null;

        state.falseButton = true;
        state.trueButton = true;
        state.cheatButton = true;
        state.nextButton = false;

        // refresh the display with updated state
        view.get().displayQuestionData(state);
    }

    @Override
    public void injectView(WeakReference<QuestionContract.View> view) {
        this.view = view;
    }

    @Override
    public void injectModel(QuestionContract.Model model) {
        this.model = model;
    }

}
