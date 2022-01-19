import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class Grabadora {
    private boolean isCapturing;
    private static final int BUFFER_SIZE = 1000;
    private OutputStream outputStream;
    private Socket socket;

    public Grabadora() throws IOException {

    }

    public void captureSound() throws LineUnavailableException, IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        isCapturing = true;

        //Definir el formato audio
        AudioFormat audioFormat = getAudioFormat();

        // Info sobre la linea
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);

        //Creamos la linea por donde escuchar el audio, pasandole la info del audio q queremos escuchar
        TargetDataLine targetDataLine = (TargetDataLine) AudioSystem.getLine(info);

        //Abrimos la linea y empezamos a caputar el sonido.
        targetDataLine.open(audioFormat);
        targetDataLine.start();

        while (isCapturing) {
            socket = new Socket("localhost",1234);
            outputStream =socket.getOutputStream();
            int read = targetDataLine.read(buffer, 0, BUFFER_SIZE/2); // la info se guarda en tempBuffer
            if (read > 0) {
                outputStream.write(buffer, 0, read);
            }
            outputStream.close();
        }
    }

    private AudioFormat getAudioFormat() {
        return new AudioFormat(16000, 8, 1, true, true);
    }

}
