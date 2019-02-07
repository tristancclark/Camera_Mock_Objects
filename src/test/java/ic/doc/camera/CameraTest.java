package ic.doc.camera;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

public class CameraTest {

  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  WriteListener cameraWriteListener = context.mock(WriteListener.class);
  Sensor cameraSensor = context.mock(Sensor.class);
  MemoryCard cameraMemoryCard = context.mock(MemoryCard.class);
  Camera camera = new Camera(cameraSensor, cameraMemoryCard);
  byte[] data = new byte[0];

  @Test
  public void switchingTheCameraOnPowersUpTheSensor() {

    context.checking(new Expectations() {{
      exactly(1).of(cameraSensor).powerUp();
    }});
    camera.powerOn();
  }

  @Test
  public void switchingTheCameraOffPowersDownTheSensor() {

    context.checking(new Expectations() {{
      exactly(1).of(cameraSensor).powerDown();
    }});

    camera.powerOff();
  }

  @Test
  public void pressingTheShutterWhenThePowerIsOffDoesNothing() {

    context.checking(new Expectations() {{
      never(cameraSensor);
      never(cameraMemoryCard);
    }});

    assertFalse(camera.isOn());
    camera.pressShutter();
  }

  @Test
  public void pressingShutterWhenPowerOnCopiesDataFromSensorToMemCard() {

    context.checking(new Expectations() {{
      exactly(1).of(cameraSensor).powerUp();
      exactly(1).of(cameraSensor).readData();
      will(returnValue(data));
      exactly(1).of(cameraMemoryCard).write(data);
    }});

    camera.powerOn();
    assertTrue(camera.isOn());
    camera.pressShutter();
  }

  @Test
  public void ifDataIsCurrentlyBeingWrittenSwitchingOffPowerDoesNotPowerDownSensor() {

    context.checking(new Expectations() {{
      exactly(1).of(cameraSensor).powerUp();
      exactly(1).of(cameraSensor).readData();
      will(returnValue(data));
      exactly(1).of(cameraMemoryCard).write(data);
      never(cameraSensor).powerDown();
    }});

    camera.powerOn();
    camera.pressShutter();
    camera.powerOff();
  }

  @Test
  public void onceWritingDataHasCompletedCameraPowersDownIfPressed() {

    context.checking(new Expectations() {{
      exactly(1).of(cameraSensor).powerUp();
      exactly(1).of(cameraSensor).readData();
      will(returnValue(data));
      exactly(1).of(cameraMemoryCard).write(data);
      exactly(1).of(cameraSensor).powerDown();
    }});

    camera.powerOn();
    camera.pressShutter();
    camera.powerOff();
    camera.writeComplete();
  }
}
