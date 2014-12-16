package it.capitanilproductions.remi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.os.AsyncTask;

public class ExportData extends AsyncTask<File, Void, Void> {

	MasterActivity context;
	
	public ExportData(MasterActivity that){
		super();
		context=that;
	}

	@Override
	protected Void doInBackground(File... params) {
//		File sourceDir=params[0];
		File destinationDir=params[1];
		File source, destination;
		int len=params.length;
		for(int i=2; i<len; i++){
			source=params[i];
			destination=new File(destinationDir, source.getName());
			
			try {
				copy(source, destination);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return null;
	}
	
	private void copy(File src, File dst) throws IOException {
	    InputStream in = new FileInputStream(src);
	    OutputStream out = new FileOutputStream(dst);

	    // Transfer bytes from in to out
	    byte[] buf = new byte[1024];
	    int len;
	    while ((len = in.read(buf)) > 0) {
	        out.write(buf, 0, len);
	    }
	    in.close();
	    out.close();
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		context.postExport();
	}
}
