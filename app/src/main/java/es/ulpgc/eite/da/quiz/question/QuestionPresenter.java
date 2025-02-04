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

    public QuestionPresenter(AppMediator mediator) {
        this.mediator = mediator;
    }

    @Override
    public void onCreateCalled() {
        Log.e(TAG, "onCreateCalled");

        // init the screen state
        state = new QuestionState();
        state.resultText = model.getEmptyResultText();
        state.questionText = model.getCurrentQuestion();
    }


    @Override
    public void onRecreateCalled() {
        Log.e(TAG, "onRecreateCalled");

        state = mediator.getQuestionState();
        model.setCurrentIndex(state.quizIndex);
        
        int savedTime = state.remainingTime;

        if (savedTime > 0) {
            resumeCountdown(savedTime);
        } else if (!state.nextButton) {
            updateResultText(state.resultText);
        }
    }


    @Override
    public void onResumeCalled() {
        Log.e(TAG, "onResumeCalled");

        // Obtener el estado guardado desde la pantalla Cheat
        CheatToQuestionState savedState = mediator.getCheatToQuestionState();
        if (savedState != null) {
            // Si el usuario vio la respuesta
            if (savedState.cheated) {
                // Si estamos en la última pregunta, solo deshabilitar botones
                if (model.isLastQuestion()) {
                    disableAnswerButtons();
                    state.nextButton = false; // Mantener deshabilitado

                } else {
                    // Avanzar a la siguiente pregunta si no es la última
                    nextButtonClicked();
                    return;
                }

            }
        }

        // Refrescar la pantalla con el estado actualizado
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

    }

    private void resumeCountdown(int timeLeft) {
        model.processAnswerWithCountdown(true,
            new QuestionContract.Model.OnAnswerProcessedWithCountdownListener() {

            @Override
            public void onTimeUpdate(int secsRemaining) {
                state.resultText = "" + secsRemaining;
                state.remainingTime=secsRemaining;
                view.get().displayQuestionData(state);
            }

                @Override
                public void onAnswerProcessed(String resultText) {
                    // Restablecer contador después de respuesta
                    state.remainingTime= 0;
                    updateResultText(resultText);
                }

        }, timeLeft);
    }


    private void updateResultText(String resultText) {
        state.resultText = resultText;
        state.nextButton = !model.isLastQuestion();
        view.get().displayQuestionData(state);
    }

    private void processAnswerWithCountdown(boolean userAnswer) {

        disableAnswerButtons();
        view.get().displayQuestionData(state);

        // Obtener tiempo restante desde Mediator
        int resumeTime = state.remainingTime;

        model.processAnswerWithCountdown(userAnswer,
            new QuestionContract.Model.OnAnswerProcessedWithCountdownListener() {

                @Override
                public void onTimeUpdate(int secsRemaining) {
                    state.resultText = "" + secsRemaining;
                    state.remainingTime = secsRemaining;
                    view.get().displayQuestionData(state);
                }

                @Override
                public void onAnswerProcessed(String resultText) {
                    state.remainingTime = 0;
                    updateResultText(resultText);
                }

            }, resumeTime);
    }


    @Override
    public void trueButtonClicked() {
        Log.e(TAG, "trueButtonClicked");

        processAnswerWithCountdown(true);
    }

    @Override
    public void falseButtonClicked() {
        Log.e(TAG, "falseButtonClicked");

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

        enableAnswerButtons();
        state.nextButton = false;

        // refresh the display with updated state
        view.get().displayQuestionData(state);
    }

    private void enableAnswerButtons() {
        state.falseButton = true;
        state.trueButton = true;
        state.cheatButton = true;
    }

    private void disableAnswerButtons() {
        state.falseButton = false;
        state.trueButton = false;
        state.cheatButton = false;
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
