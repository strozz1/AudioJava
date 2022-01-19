import javax.sound.sampled.*;
import java.io.*;

public class Main {
    private static TargetDataLine targetDataLine;
    private static AudioInputStream audioInputStream;
    private AudioFormat audioFormat;
    private SourceDataLine sourceDataLine;
    private ByteArrayOutputStream byteArrayOutputStream;
    private boolean capture;

    public static void main(String[] args) throws LineUnavailableException, InterruptedException {
        new Main();
    }

    public Main() throws InterruptedException, LineUnavailableException {
            captureSound();
    }

    public void playAudio() {
        try {
            System.out.println("dentro de play audio");
            // Cogemos la informacion guardada anteriormente
//            byte[] audioData = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.reset();
//            InputStream byteArrayInputStream = new ByteArrayInputStream(audioData);

            //Creamos un formato
            AudioFormat audioFormat = getAudioFormat();

//            audioInputStream = new AudioInputStream(byteArrayInputStream, audioFormat, audioData.length / audioFormat.getFrameSize());

            DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);

            sourceDataLine = (SourceDataLine) AudioSystem.getLine(info);
            sourceDataLine.open(audioFormat);
            sourceDataLine.start();

            Thread hiloReproducir = new HiloDeReproduccion();
            hiloReproducir.start();

        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void captureSound() throws LineUnavailableException {
        //Tipo de archivo q vamos a crear, en caso de querer guardarlo en un archivo.
        File file = new File("prueba.wav");
        AudioFileFormat.Type type = AudioFileFormat.Type.WAVE;

        //Definir el formato audio
        audioFormat = getAudioFormat();

        // Info sobre la linea
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);
        DataLine.Info info2 = new DataLine.Info(SourceDataLine.class, audioFormat);

        //Creamos la linea por donde escuchar el audio, pasandole la info del audio q queremos escuchar
        sourceDataLine = (SourceDataLine) AudioSystem.getLine(info2);
        targetDataLine = (TargetDataLine) AudioSystem.getLine(info);

        //Abrimos la linea y empezamos a caputar el sonido.
        targetDataLine.open(audioFormat);
        targetDataLine.start();

        //Para reporducir sonido
        sourceDataLine.open(audioFormat);
        sourceDataLine.start();
        //Iniciamos hilo de lectura
        Thread hiloCaptura = new HiloDeCaptura();
        hiloCaptura.start();


    }

    private AudioFormat getAudioFormat() {
        return new AudioFormat(16000, 8, 1, true, true);
    }

    class HiloDeCaptura extends Thread {
        byte[] tempBuffer = new byte[SIZE];


        @Override
        public void run() {
            try {
                //creamos nuestro stream de captura
                byteArrayOutputStream = new ByteArrayOutputStream();
                //mientras capture sea true, escucharemos el microfono
                capture = true;
                while (capture) {
                    int read = targetDataLine.read(tempBuffer, 0, SIZE2); // la info se guarda en tempBuffer
                    if (read > 0) {
                        //Si hemos almacenado informacion, la guardamos en un OutputStream
                        byteArrayOutputStream.write(tempBuffer, 0, read);
                        sourceDataLine.write(tempBuffer, 0, read);
                    }
                }
                //Cerramos el stream
                byteArrayOutputStream.close();
            } catch (IOException e) {
                System.err.println("Error al guardar sonido. " + e.getMessage());
            }
        }
    }
    static int SIZE= 1000;
    static int SIZE2= 500;
    class HiloDeReproduccion extends Thread {
        byte tempBuffer[] = new byte[SIZE];

        @Override
        public void run() {
            try {
                int read;
                //Leer hasta q devuelva -1, es decir, hasta q este vacio
                while ((read = audioInputStream.read(tempBuffer, 0, SIZE2)) != -1) {
                    if (read > 0) {
                        //Escribimos la informacion en el buffer de la linea
                        sourceDataLine.write(tempBuffer, 0, read);

                    }
                }
                sourceDataLine.drain();
                sourceDataLine.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
