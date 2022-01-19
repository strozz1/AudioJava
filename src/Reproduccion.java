import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Reproduccion {
    private static final int BUFFER_SIZE = 1000;
    private AudioInputStream audioInputStream;
    private InputStream inputStream;
    private ServerSocket serverSocket;
    private Socket socket;
    private boolean listening;

    public Reproduccion() throws IOException {
        serverSocket = new ServerSocket(1234);

    }

    public void getAudio() throws IOException, LineUnavailableException {
        byte[] buffer = new byte[BUFFER_SIZE];
        listening= true;

        AudioFormat audioFormat = getAudioFormat();
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
        SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem.getLine(info);
        sourceDataLine.open(audioFormat);
        sourceDataLine.start();

        while (listening) {
            socket = serverSocket.accept();
            inputStream = socket.getInputStream();
            audioInputStream = new AudioInputStream(inputStream, audioFormat, BUFFER_SIZE / audioFormat.getFrameSize());

            int read;
            //Leer hasta q devuelva -1, es decir, hasta q este vacio
            while ((read = audioInputStream.read(buffer, 0, BUFFER_SIZE / 2)) != -1) {
                if (read > 0) {
                    //Escribimos la informacion en el buffer de la linea
                    sourceDataLine.write(buffer, 0, read);

                }
            }
            audioInputStream.close();
            inputStream.close();
        }
    }

    private AudioFormat getAudioFormat() {
        return new AudioFormat(16000, 8, 1, true, true);
    }

}
