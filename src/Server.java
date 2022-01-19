import javax.sound.sampled.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    ServerSocket MyService;
    Socket clientSocket = null;
    InputStream input;
    TargetDataLine targetDataLine;
    OutputStream out;
    AudioFormat audioFormat;
    SourceDataLine sourceDataLine;
    byte tempBuffer[] = new byte[10000];
    static Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();

    Server() throws LineUnavailableException {

        try {
            Mixer mixer_ = AudioSystem.getMixer(mixerInfo[0]);
            audioFormat = getAudioFormat();
            DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
            sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
            sourceDataLine.open(audioFormat);
            sourceDataLine.start();
            MyService = new ServerSocket(500);
            clientSocket = MyService.accept();
            captureAudio();
            input = new BufferedInputStream(clientSocket.getInputStream());
            out = new BufferedOutputStream(clientSocket.getOutputStream());
            while (input.read(tempBuffer) != -1) {
                sourceDataLine.write(tempBuffer, 0, 10000);

            }

        } catch (IOException e) {

            e.printStackTrace();
        }

    }

    private AudioFormat getAudioFormat() {
        float sampleRate = 8000.0F;
        int sampleSizeInBits = 8;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = false;
        return new AudioFormat(
                sampleRate,
                sampleSizeInBits,
                channels,
                signed,
                bigEndian);
    }

    public static void main(String s[]) throws LineUnavailableException {
        Server s2 = new Server();
    }

    private void captureAudio() {
        try {

            audioFormat = getAudioFormat();
            DataLine.Info dataLineInfo = new DataLine.Info(
                    TargetDataLine.class, audioFormat);
            Mixer mixer = null;
            System.out.println("Available mixers:");
            for (int cnt = 0; cnt < mixerInfo.length; cnt++) {
                mixer = AudioSystem.getMixer(mixerInfo[3]);
                if (mixer.isLineSupported(dataLineInfo)) {
                    System.out.println(mixerInfo[cnt].getName());
                    targetDataLine = (TargetDataLine) mixer.getLine(dataLineInfo);
                }
            }
            targetDataLine.open(audioFormat);
            targetDataLine.start();

            Thread captureThread = new CaptureThread();
            captureThread.start();
        } catch (Exception e) {
            System.out.println(e);
            System.exit(0);
        }
    }

    class CaptureThread extends Thread {

        byte tempBuffer[] = new byte[10000];

        @Override
        public void run() {
            try {
                while (true) {
                    int cnt = targetDataLine.read(tempBuffer, 0, tempBuffer.length);
                    out.write(tempBuffer);
                    out.flush();

                }

            } catch (Exception e) {
                System.out.println(e);
                System.exit(0);
            }
        }
    }}