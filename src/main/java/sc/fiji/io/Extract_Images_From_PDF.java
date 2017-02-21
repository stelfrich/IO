/**
 * Copyright Albert Cardona 2008.
 * Released under the General Public License in its latest version.
 *
 * Modeled after scripts/pdf-extract-images.py by Johannes Schindelin
 */

package sc.fiji.io;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.PlugIn;

/**
 * Extract all images from a PDF file (or from an URL given as argument), and
 * open them all within ImageJ in their original resolution.
 */
public class Extract_Images_From_PDF implements PlugIn {

	@Override
	public void run(String arg) {

		final String path = PDF_Viewer.getPath(arg);
		if (null == path) return;

		PDDocument document = null;
		try {
			document = PDDocument.load(new File(path));

			int pageNum = 0;
			for (PDPage page : document.getPages()) {
				IJ.showStatus("Decoding page " + pageNum);

				PDResources pdResources = page.getResources();
				for (COSName cosName : pdResources.getXObjectNames()) {
					PDXObject object = pdResources.getXObject(cosName);
					if (object instanceof PDImageXObject) {
						String imgName = cosName.getName();
						BufferedImage image = ((PDImageXObject) object).getImage();
						new ImagePlus(imgName, image).show();
					}
				}

				pageNum++;
			}
		}
		catch (IOException e) {
			IJ.log("Error: " + e);
			e.printStackTrace();
		}

		IJ.showStatus("Done.");
	}

}
