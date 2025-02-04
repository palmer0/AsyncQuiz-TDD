package es.ulpgc.eite.da.quiz;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.os.RemoteException;
import android.os.SystemClock;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.concurrent.TimeUnit;

import es.ulpgc.eite.da.quiz.app.AppMediator;
import es.ulpgc.eite.da.quiz.question.QuestionActivity;

// Project: Quiz
// Created by Luis Hernandez 
// Copyright (c) 2025 EITE (ULPGC)
@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EspressoQuizTests {

    int delayMillis = 10000;


    @Before
    public void setUp() {
        // Reiniciar el estado antes de cada test
        AppMediator.resetInstance();
    }


    private void rotateScreen() {

        // Obtener el dispositivo UI Automator
        UiDevice device =
            UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        try {

            // Simular giro de pantalla a landscape
            device.setOrientationLeft();

            // Restaurar la orientación natural de la pantalla
            device.setOrientationNatural();

        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }


    }



    @Test
    // Verifica que al responder correctamente, se muestra el texto adecuado
    public void test01_InitialState() {

        // Iniciar la actividad
        ActivityScenario.launch(QuestionActivity.class);

        // Verificar que la primera pregunta se muestra correctamente
        onView(withId(R.id.questionField))
            .check(matches(withText(
                "Christian Bale played Batman in 'The Dark Knight Rises'?"
            )));

        // Verificar que los botones de respuesta están habilitados
        onView(withId(R.id.trueButton))
            .check(matches(ViewMatchers.isEnabled()));
        onView(withId(R.id.falseButton))
            .check(matches(ViewMatchers.isEnabled()));

        // Verificar que el botón "Next" está deshabilitado
        onView(withId(R.id.nextButton))
            .check(matches(ViewMatchers.isNotEnabled()));
    }


    @Test
    // Verifica que al responder incorrectamente,
    // se muestra el texto de respuesta incorrecta
    public void test02_AnswerQuestionAndRotate() {

        // Iniciar la actividad
        ActivityScenario.launch(QuestionActivity.class);

        // Responder correctamente a la primera pregunta
        onView(withId(R.id.trueButton)).perform(ViewActions.click());

        try {
            Thread.sleep(delayMillis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Verificar que el resultado es correcto
        onView(withId(R.id.resultField))
            .check(matches(withText("Correct!")));

        // Girar la pantalla
        rotateScreen();

        // Verificar que el estado se conserva después del giro
        onView(withId(R.id.resultField))
            .check(matches(withText("Correct!")));
        onView(withId(R.id.trueButton))
            .check(matches(ViewMatchers.isNotEnabled()));
        onView(withId(R.id.falseButton))
            .check(matches(ViewMatchers.isNotEnabled()));
        onView(withId(R.id.nextButton))
            .check(matches(ViewMatchers.isEnabled()));
    }


    @Test
    // Verifica que el estado de la pantalla se mantiene después de una rotación real
    public void test03_CheatAndRotate() {

        // Iniciar la actividad
        ActivityScenario.launch(QuestionActivity.class);

        // Navegar a la pantalla de "Cheat"
        onView(withId(R.id.cheatButton)).perform(ViewActions.click());

        // Hacer clic en "Yes" para ver la respuesta
        onView(withId(R.id.yesButton)).perform(ViewActions.click());

        // Girar la pantalla
        rotateScreen();

        // Verificar que el estado se conserva después del giro
        onView(withId(R.id.answerField))
            .check(matches(withText("True")));
        onView(withId(R.id.yesButton))
            .check(matches(ViewMatchers.isNotEnabled()));
        onView(withId(R.id.noButton))
            .check(matches(ViewMatchers.isNotEnabled()));
    }


    @Test
    // Verifica que al hacer trampa y regresar,
    // se avanza automáticamente a la siguiente pregunta
    public void test04_LastQuestionAndRotate() {

        // Iniciar la actividad
        ActivityScenario.launch(QuestionActivity.class);

        // Navegar a la última pregunta suponiendo que hay 4 preguntas
        for (int i = 0; i < 4; i++) {
            onView(withId(R.id.trueButton)).perform(ViewActions.click());

            try {
                Thread.sleep(delayMillis);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            onView(withId(R.id.nextButton)).perform(ViewActions.click());
        }

        // Verificar que la ultima pregunta se muestra correctamente
        onView(withId(R.id.questionField))
            .check(matches(withText(
                "The Teenage Mutant Ninja Turtles are named after famous artists?"
            )));

        // Responder a la última pregunta
        onView(withId(R.id.trueButton)).perform(ViewActions.click());


        try {
            Thread.sleep(delayMillis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        // Girar la pantalla
        rotateScreen();

        // Verificar que el estado se conserva después del giro
        onView(withId(R.id.resultField))
            .check(matches(withText("Correct!")));
        onView(withId(R.id.nextButton))
            .check(matches(ViewMatchers.isNotEnabled()));
    }


    // Verifica que el estado en la pantalla Cheat se mantiene tras girar la pantalla
    @Test
    public void test05_CheatScreenRotationMaintainsState() {

        // Iniciar la actividad
        ActivityScenario.launch(QuestionActivity.class);

        onView(withId(R.id.cheatButton)).perform(ViewActions.click());

        // Girar la pantalla
        rotateScreen();

        onView(withId(R.id.confirmationField))
            .check(matches(ViewMatchers.isDisplayed()));
    }

    // Verifica que al regresar de la pantalla Cheat sin hacer trampa,
    // los botones de respuesta siguen habilitados
    @Test
    public void test06_GoToCheatScreenAndReturnWithoutCheating() {

        // Iniciar la actividad
        ActivityScenario.launch(QuestionActivity.class);

        onView(withId(R.id.cheatButton)).perform(ViewActions.click());

        // Girar la pantalla
        rotateScreen();

        // Regresar a la pantalla Question
        pressBack();

        onView(withId(R.id.trueButton))
            .check(matches(ViewMatchers.isEnabled()));
        onView(withId(R.id.falseButton))
            .check(matches(ViewMatchers.isEnabled()));
    }


    @Test
    // Verifica que desde la última pregunta se puede acceder
    // a la pantalla Cheat y ver la respuesta
    public void test07_LastQuestionAndCheat() {


        // Iniciar la actividad
        ActivityScenario.launch(QuestionActivity.class);

        // Navegar a la última pregunta suponiendo que hay 4 preguntas
        for (int i = 0; i < 4; i++) {
            onView(withId(R.id.trueButton)).perform(ViewActions.click());


            try {
                Thread.sleep(delayMillis);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }


            onView(withId(R.id.nextButton)).perform(ViewActions.click());
        }

        // Girar la pantalla
        rotateScreen();

        // Verificar que la ultima pregunta se muestra correctamente
        onView(withId(R.id.questionField))
            .check(matches(withText(
                "The Teenage Mutant Ninja Turtles are named after famous artists?"
            )));

        // Acceder a la pantalla Cheat desde la última pregunta
        onView(withId(R.id.cheatButton)).perform(ViewActions.click());

        // Ver la respuesta en la pantalla Cheat
        onView(withId(R.id.yesButton)).perform(ViewActions.click());

        // Girar la pantalla
        rotateScreen();

        // Verificar que la respuesta se muestra en la pantalla Cheat
        onView(withId(R.id.answerField))
            .check(matches(ViewMatchers.isDisplayed()));

    }


    // Verifica que desde la última pregunta se puede acceder
    // a la pantalla Cheat y ver la respuesta, y que al regresar
    // a la pantalla Question se actualiza correctamente el estado
    @Test
    public void test08_LastQuestionAndCheatAndBack() {

        // Iniciar la actividad
        ActivityScenario.launch(QuestionActivity.class);


        // Navegar a la última pregunta suponiendo que hay 4 preguntas
        for (int i = 0; i < 4; i++) {
            onView(withId(R.id.trueButton)).perform(ViewActions.click());


            try {
                Thread.sleep(delayMillis);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }


            onView(withId(R.id.nextButton)).perform(ViewActions.click());
        }

        // Girar la pantalla
        rotateScreen();

        // Verificar que la ultima pregunta se muestra correctamente
        onView(withId(R.id.questionField))
            .check(matches(withText(
                "The Teenage Mutant Ninja Turtles are named after famous artists?"
            )));

        // Acceder a la pantalla Cheat desde la última pregunta
        onView(withId(R.id.cheatButton)).perform(ViewActions.click());

        // Ver la respuesta en CheatActivity
        onView(withId(R.id.yesButton)).perform(ViewActions.click());

        // Girar la pantalla
        rotateScreen();

        // Verificar que la respuesta se muestra en CheatActivity
        onView(withId(R.id.answerField))
            .check(matches(ViewMatchers.isDisplayed()));

        // Regresar a la pantalla Question
        pressBack();

        // Girar la pantalla
        rotateScreen();

        // Verificar que la pregunta sigue visible
        onView(withId(R.id.questionField))
            .check(matches(withText(
                "The Teenage Mutant Ninja Turtles are named after famous artists?"
            )));

        // Verificar que los botones de respuesta están deshabilitados
        // después de ver la  respuesta
        onView(withId(R.id.trueButton))
            .check(matches(ViewMatchers.isNotEnabled()));
        onView(withId(R.id.falseButton))
            .check(matches(ViewMatchers.isNotEnabled()));

        // Verificar que el botón "Next" sigue deshabilitado (ya que es la última pregunta)
        onView(withId(R.id.nextButton))
            .check(matches(ViewMatchers.isNotEnabled()));

        // Verificar que el botón "Cheat" sigue deshabilitado
        // (ya que se ha visto la  respuesta a la  pregunta)
        onView(withId(R.id.nextButton))
            .check(matches(ViewMatchers.isNotEnabled()));
    }


}
