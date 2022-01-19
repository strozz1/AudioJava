import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class Main {
    public static TargetDataLine mic;

    public static void main(String[] args) throws LineUnavailableException, IOException {
        File file = new File("prueba.wav");
        AudioFileFormat.Type type = AudioFileFormat.Type.WAVE;

        AudioFormat audioFormat = new AudioFormat(16000, 8, 1, true, true);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class,audioFormat);
        mic = (TargetDataLine) AudioSystem.getLine(info);


        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("hilo iniciado");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mic.stop();
                mic.close();
                System.out.println("cerrando y guardando");
            }
        }).start();
        mic.open();
        mic.start();
        System.out.println("Escuchando");
        AudioInputStream audioInputStream = new AudioInputStream(mic);
        AudioSystem.write(audioInputStream, type, file);
    }
}
