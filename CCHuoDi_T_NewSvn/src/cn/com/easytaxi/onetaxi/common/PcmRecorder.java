package cn.com.easytaxi.onetaxi.common;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;
import cn.com.easytaxi.AppLog;
import cn.com.easytaxi.ETApp;
import cn.com.easytaxi.onetaxi.MainActivityNew;

public class PcmRecorder implements Runnable {

	// private Logger log = LoggerFactory.getLogger(PcmRecorder.class);
	private volatile boolean isRecording;
	private final Object mutex = new Object();
	private static final int frequency = 16000;
	private static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
	// private SpeexEncoder speexEncoder;
	private int frameSize;
	// private AudioFileWriter writer;
	private int quality = 8;
	private byte[] processedData = new byte[1024];
	private int payloadSize;

	private RandomAccessFile randomAccessWriter;

	public PcmRecorder() {
		super();

	}

	public void run() {

		// Æô¶¯±àÂëÏß³Ì
		// Encoder encoder = new Encoder();
		// speexEncoder = new SpeexEncoder(FrequencyBand.WIDE_BAND, quality);
		// frameSize = speexEncoder.getFrameSize();
		String dir = ETApp.getInstance().getMobileInfo().getSDCardPath();
		String soundPath = dir + MainActivityNew.SOUND_FILE_NAME_spx;
		System.out.println("frameSize  ===  " + frameSize);

		createWavFileHeader(soundPath);
		// writer = new OggSpeexWriter(1, frequency, 1, 1, false);
		// try {
		// writer.open(soundPath);
		// writer.writeHeader("Encoded with: myself");
		// } catch (IOException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }

		synchronized (mutex) {
			while (!this.isRecording) {
				try {
					mutex.wait();
				} catch (InterruptedException e) {
					throw new IllegalStateException("Wait() interrupted!", e);
				}
			}
		}
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

		int bufferRead = 0;
		int bufferSize = AudioRecord.getMinBufferSize(frequency, AudioFormat.CHANNEL_IN_MONO, audioEncoding);

		byte[] tempBuffer = new byte[bufferSize];
		AudioRecord recordInstance = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency, AudioFormat.CHANNEL_IN_MONO, audioEncoding,
				bufferSize);

		recordInstance.startRecording();

		while (this.isRecording) {
//			Log.d("---", " ---   111111111" + " , ");
			bufferRead = recordInstance.read(tempBuffer, 0, bufferSize);
//			Log.d("---", " ---   111111111" + " , " + bufferRead);
			if (bufferRead == AudioRecord.ERROR_INVALID_OPERATION) {
				throw new IllegalStateException("read() returned AudioRecord.ERROR_INVALID_OPERATION");
			} else if (bufferRead == AudioRecord.ERROR_BAD_VALUE) {
				throw new IllegalStateException("read() returned AudioRecord.ERROR_BAD_VALUE");
			} else if (bufferRead == AudioRecord.ERROR_INVALID_OPERATION) {
				throw new IllegalStateException("read() returned AudioRecord.ERROR_INVALID_OPERATION");
			}
			try {
				
//				Log.d("---", " --- bufferRead " + bufferRead + " , " + bufferSize);
				randomAccessWriter.write(tempBuffer); // Write buffer to file
				payloadSize += tempBuffer.length;
//				Log.d("---", " --- payloadSize " + payloadSize + " , ");

			} catch (Exception e) {
				// TODO: handle exception
			}

			// if (bufferRead < frameSize) {
			// continue;
			//
			// } else {

			
			// short[] realData = new short[bufferRead / 2];
			// System.arraycopy(tempBuffer, 0, realData, 0, bufferRead / 2);
			// // processedData = speexEncoder.encode(realData);
			// try {
			// writer.writePacket(processedData, 0, processedData.length);
			// } catch (IOException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			//
			// System.arraycopy(tempBuffer, bufferRead / 2, realData, 0,
			// bufferRead / 2);
			// processedData = speexEncoder.encode(realData);
			// try {
			// writer.writePacket(processedData, 0, processedData.length);
			// } catch (IOException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// }

		}
		try {

			Log.d("---", " -------------- writer.close(); " + " , ");
			// writer.close();

			randomAccessWriter.seek(4); // Write size to RIFF header
			randomAccessWriter.writeInt(Integer.reverseBytes(36 + payloadSize));

			randomAccessWriter.seek(40); // Write size to Subchunk2Size field
			randomAccessWriter.writeInt(Integer.reverseBytes(payloadSize));

			randomAccessWriter.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		recordInstance.stop();
		recordInstance.release();
		recordInstance = null;
	}

	private void createWavFileHeader(String filePath) {
		AppLog.LogD("createWavFileHeader-=---------------");
		
		
		try {

			File file = new File(filePath);
			
			if(file.exists()){
				
				AppLog.LogD("00000000000000009-=---------------");
				file.delete();
			}
			
			file.createNewFile();
			
			randomAccessWriter = new RandomAccessFile(filePath, "rw");

			randomAccessWriter.setLength(0); // Set file length to 0, to prevent
												// unexpected behavior in case
												// the file already existed
			randomAccessWriter.writeBytes("RIFF");
			randomAccessWriter.writeInt(0); // Final file size not known yet,
											// write 0
			randomAccessWriter.writeBytes("WAVE");
			randomAccessWriter.writeBytes("fmt ");
			randomAccessWriter.writeInt(Integer.reverseBytes(16)); // Sub-chunk
																	// size, 16
																	// for PCM
			randomAccessWriter.writeShort(Short.reverseBytes((short) 1)); // AudioFormat,
																			// 1
																			// for
																			// PCM
			randomAccessWriter.writeShort(Short.reverseBytes((short) 1));// Number
																			// of
																			// channels,
																			// 1
																			// for
																			// mono,
																			// 2
																			// for
																			// stereo
			randomAccessWriter.writeInt(Integer.reverseBytes(16000)); // Sample
																		// rate
			randomAccessWriter.writeInt(Integer.reverseBytes(16000 * 16 * 1 / 8)); // Byte
																					// rate,
																					// SampleRate*NumberOfChannels*BitsPerSample/8
			randomAccessWriter.writeShort(Short.reverseBytes((short) (1 * 16 / 8))); // Block
																						// align,
																						// NumberOfChannels*BitsPerSample/8
			randomAccessWriter.writeShort(Short.reverseBytes((short) 16)); // Bits
																			// per
																			// sample
			randomAccessWriter.writeBytes("data");
			randomAccessWriter.writeInt(0); // Data chunk size not known yet,
											// write 0
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
	}

	public void setRecording(boolean isRecording) {
		synchronized (mutex) {
			this.isRecording = isRecording;
			if (this.isRecording) {
				mutex.notify();
			}
		}
	}

	public boolean isRecording() {
		synchronized (mutex) {
			return isRecording;
		}
	}
}
