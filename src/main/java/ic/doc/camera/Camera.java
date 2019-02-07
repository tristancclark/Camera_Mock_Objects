package ic.doc.camera;

public class Camera implements WriteListener {

  private Sensor cameraSensor;
  private MemoryCard cameraMemoryCard;
  private boolean power;
  private boolean dataBeingWritten;

  public Camera(Sensor cameraSensor, MemoryCard cameraMemoryCard) {
    this.cameraSensor = cameraSensor;
    this.cameraMemoryCard = cameraMemoryCard;
    this.power = false;
    this.dataBeingWritten = false;
  }

  public void pressShutter() {
    if (power) {
      dataBeingWritten = true;
      byte[] data = cameraSensor.readData();
      cameraMemoryCard.write(data);
    }
  }

  public void powerOn() {
    power = true;
    cameraSensor.powerUp();
  }

  public void powerOff() {
    if (!dataBeingWritten) {
      cameraSensor.powerDown();
    }
    power = false;
  }

  public boolean isOn() {
    return power;
  }

  @Override
  public void writeComplete() {
    dataBeingWritten = false;
    cameraSensor.powerDown();
  }
}

