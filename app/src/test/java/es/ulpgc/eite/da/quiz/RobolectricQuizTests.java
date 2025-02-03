package es.ulpgc.eite.da.quiz;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

import android.content.Intent;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLooper;
import org.robolectric.shadows.ShadowAsyncTask;

import java.time.Duration;

import es.ulpgc.eite.da.quiz.app.AppMediator;
import es.ulpgc.eite.da.quiz.cheat.CheatActivity;
import es.ulpgc.eite.da.quiz.question.QuestionActivity;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {28})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RobolectricQuizTests {

    private QuestionActivity activity;
    private TextView questionField;
    private TextView resultField;
    private Button trueButton;
    private Button falseButton;
    private Button nextButton;
    private Button cheatButton;

    @Before
    public void setUp() {
        // Reiniciar el estado antes de cada test
        AppMediator.resetInstance();

        // Iniciar la actividad
        activity = Robolectric
            .buildActivity(QuestionActivity.class)
            .create().visible().get();

        // Obtener referencias a las vistas
        questionField = activity.findViewById(R.id.questionField);
        resultField = activity.findViewById(R.id.resultField);
        trueButton = activity.findViewById(R.id.trueButton);
        falseButton = activity.findViewById(R.id.falseButton);
        nextButton = activity.findViewById(R.id.nextButton);
        cheatButton = activity.findViewById(R.id.cheatButton);
    }

    @Test
    public void test01_InitialState() {
        // Verificar que la primera pregunta se muestra correctamente
        assertEquals(
            "Christian Bale played Batman in 'The Dark Knight Rises'?",
            questionField.getText().toString()
        );

        // Verificar que los botones de respuesta están habilitados
        assertTrue(trueButton.isEnabled());
        assertTrue(falseButton.isEnabled());

        // Verificar que el botón "Next" está deshabilitado
        assertFalse(nextButton.isEnabled());
    }

    @Test
    public void test02_AnswerQuestionAndRotate() {
        // Responder correctamente a la primera pregunta
        trueButton.performClick();


        // Verificar que el resultado es correcto después del delay
        assertEquals("Correct!", resultField.getText().toString());


        // Girar la pantalla (simular cambio de configuración)
        activity.recreate();

        // Verificar que el estado se conserva después del giro
        assertEquals("Correct!", resultField.getText().toString());
        assertFalse(trueButton.isEnabled());
        assertFalse(falseButton.isEnabled());
        assertTrue(nextButton.isEnabled());
    }


    @Test
    public void test03_CheatAndRotate() {
        // Navegar a la pantalla de "Cheat"
        cheatButton.performClick();

        // Obtener el Intent que se lanzó
        Intent startedIntent = shadowOf(activity).getNextStartedActivity();
        assertNotNull(startedIntent);

        // Crear y lanzar la actividad Cheat usando el Intent
        CheatActivity cheatActivity = Robolectric
            .buildActivity(CheatActivity.class, startedIntent)
            .create().visible().get();

        // Hacer clic en "Yes" para ver la respuesta
        Button yesButton = cheatActivity.findViewById(R.id.yesButton);
        yesButton.performClick();

        // Girar la pantalla (simular cambio de configuración)
        cheatActivity.recreate();

        // Verificar que el estado se conserva después del giro
        TextView answerField = cheatActivity.findViewById(R.id.answerField);
        assertEquals("True", answerField.getText().toString());
        assertFalse(yesButton.isEnabled());
        assertFalse(cheatActivity.findViewById(R.id.noButton).isEnabled());
    }

    @Test
    public void test04_LastQuestionAndRotate() {
        // Navegar a la última pregunta suponiendo que hay 4 preguntas
        for (int i = 0; i < 4; i++) {
            trueButton.performClick();
            nextButton.performClick();
        }

        // Verificar que la última pregunta se muestra correctamente
        assertEquals(
            "The Teenage Mutant Ninja Turtles are named after famous artists?",
            questionField.getText().toString()
        );

        // Responder a la última pregunta
        trueButton.performClick();

        // Girar la pantalla (simular cambio de configuración)
        activity.recreate();

        // Verificar que el estado se conserva después del giro
        assertEquals("Correct!", resultField.getText().toString());
        assertFalse(nextButton.isEnabled());
    }

    @Test
    public void test05_CheatScreenRotationMaintainsState() {
        // Navegar a la pantalla de "Cheat"
        cheatButton.performClick();

        // Obtener el Intent que se lanzó
        Intent startedIntent = shadowOf(activity).getNextStartedActivity();
        assertNotNull(startedIntent);

        // Crear y lanzar la actividad Cheat usando el Intent
        CheatActivity cheatActivity = Robolectric
            .buildActivity(CheatActivity.class, startedIntent)
            .create().visible().get();

        // Girar la pantalla (simular cambio de configuración)
        cheatActivity.recreate();

        // Verificar que el estado se conserva después del giro
        assertTrue(cheatActivity.findViewById(R.id.confirmationField).isShown());
    }

    @Test
    public void test06_GoToCheatScreenAndReturnWithoutCheating() {
        // Navegar a la pantalla de "Cheat"
        cheatButton.performClick();

        // Regresar a la pantalla principal
        activity.onBackPressed();

        // Verificar que los botones de respuesta siguen habilitados
        assertTrue(trueButton.isEnabled());
        assertTrue(falseButton.isEnabled());
    }

    @Test
    public void test07_LastQuestionAndCheat() {
        // Navegar a la última pregunta suponiendo que hay 4 preguntas
        for (int i = 0; i < 4; i++) {
            trueButton.performClick();
            nextButton.performClick();
        }

        // Verificar que la última pregunta se muestra correctamente
        assertEquals(
            "The Teenage Mutant Ninja Turtles are named after famous artists?",
            questionField.getText().toString()
        );

        // Acceder a la pantalla Cheat desde la última pregunta
        cheatButton.performClick();

        // Obtener el Intent que se lanzó
        Intent startedIntent = shadowOf(activity).getNextStartedActivity();
        assertNotNull(startedIntent);

        // Crear y lanzar la actividad Cheat usando el Intent
        CheatActivity cheatActivity = Robolectric
            .buildActivity(CheatActivity.class, startedIntent)
            .create().visible().get();

        // Ver la respuesta en la pantalla Cheat
        Button yesButton = cheatActivity.findViewById(R.id.yesButton);
        yesButton.performClick();

        // Verificar que la respuesta se muestra en la pantalla Cheat
        assertTrue(cheatActivity.findViewById(R.id.answerField).isShown());
    }

    @Test
    public void test08_LastQuestionAndCheatAndBack() {
        // Navegar a la última pregunta suponiendo que hay 4 preguntas
        for (int i = 0; i < 4; i++) {
            trueButton.performClick();
            nextButton.performClick();
        }

        // Verificar que la última pregunta se muestra correctamente
        assertEquals(
            "The Teenage Mutant Ninja Turtles are named after famous artists?",
            questionField.getText().toString()
        );

        // Acceder a la pantalla Cheat desde la última pregunta
        cheatButton.performClick();

        // Obtener el Intent que se lanzó
        Intent startedIntent = shadowOf(activity).getNextStartedActivity();
        assertNotNull(startedIntent);

        // Crear y lanzar la actividad Cheat usando el Intent
        CheatActivity cheatActivity = Robolectric
            .buildActivity(CheatActivity.class, startedIntent)
            .create().visible().get();

        // Ver la respuesta en CheatActivity
        Button yesButton = cheatActivity.findViewById(R.id.yesButton);
        yesButton.performClick();

        // Verificar que la respuesta se muestra en CheatActivity
        assertTrue(cheatActivity.findViewById(R.id.answerField).isShown());

        // Regresar a la pantalla Question
        cheatActivity.onBackPressed();

        // Verificar que la pregunta sigue visible
        assertEquals(
            "The Teenage Mutant Ninja Turtles are named after famous artists?",
            questionField.getText().toString()
        );

        // Verificar que los botones de respuesta están deshabilitados
        assertFalse(trueButton.isEnabled());
        assertFalse(falseButton.isEnabled());

        // Verificar que el botón "Next" sigue deshabilitado
        assertFalse(nextButton.isEnabled());

        // Verificar que el botón "Cheat" sigue deshabilitado
        assertFalse(cheatButton.isEnabled());
    }
}