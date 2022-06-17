package com.app.scannergosdk

import android.content.Context
import android.os.Environment
import com.app.scannergosdk.CommonUtils.getScaledDimension
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.multipdf.Overlay
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPage
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle
import com.tom_roush.pdfbox.pdmodel.encryption.AccessPermission
import com.tom_roush.pdfbox.pdmodel.encryption.StandardProtectionPolicy
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImageXObject
import java.io.File
import java.io.IOException


class ImageToPdfHelper(context: Context, inputImage: List<String>, password: String) {
    private val context: Context
    private val inputImage: List<String>
    private val password : String
    @Throws(IOException::class)
    fun convertImageToPdf() {
        val document = PDDocument()
//        val  path = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString()
        val  path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString()
        val outPutPath = path + File.separator +"converted.pdf"
        for (imageUri in inputImage) {
            val pdPage = PDPage(PDRectangle.A5)
            document.addPage(pdPage)
            val pdPageContentStream = PDPageContentStream(document, pdPage)
            val pdImageXObject = PDImageXObject.createFromFile(imageUri, document)
            val pdfPageDim =
                DimensionUtil(PDRectangle.A5.width.toInt(), PDRectangle.A5.height.toInt())
            val imagePageDim = DimensionUtil(pdImageXObject.width, pdImageXObject.height)
            val newDim = getScaledDimension(imagePageDim, pdfPageDim)
            val imagewidth = newDim.getWidth()
            val imageheight = newDim.getHeight()
            val x = (pdPage.mediaBox.width - imagewidth) / 2
            val y = (pdPage.mediaBox.height - imageheight) / 2
            pdPageContentStream.drawImage(
                pdImageXObject,
                x,
                y,
                imagewidth.toFloat(),
                imageheight.toFloat()
            )
            pdPageContentStream.close()
        }
        document.save(outPutPath)
        document.close()
        addWaterMark(outPutPath,path)
    }

    init {
        this.context = context
        this.inputImage = inputImage
        this.password = password
        PDFBoxResourceLoader.init(context)
    }

    private fun addWaterMark(outputFile: String, saveToPath : String){
        val realDoc: PDDocument = PDDocument.load(File(outputFile))
        val waterMarkDoc: PDDocument = PDDocument.load(File(saveToPath + File.separator + "watermark.pdf"))
        val overlayGuide = HashMap<Int, String>()
        for (i in 0 until realDoc.numberOfPages) {
            overlayGuide[i] = saveToPath + File.separator + "watermark.pdf"
        }
        val overlay = Overlay()
        overlay.setInputPDF(realDoc)
        overlay.setAllPagesOverlayPDF(waterMarkDoc)
        overlay.setOverlayPosition(Overlay.Position.FOREGROUND)
        overlay.overlay(overlayGuide).save(outputFile)
        if(password.isNotEmpty()){
            encryptPdf(outputFile,saveToPath,password)
        }
    }
    private fun encryptPdf(docPath : String, saveToPath: String, password: String){
        val realDoc: PDDocument = PDDocument.load(File(docPath))
        val keyLength = 128
        val ap = AccessPermission()
        ap.setCanPrint(false)

        val spp = StandardProtectionPolicy(password, password, ap)
        spp.encryptionKeyLength = keyLength
        spp.permissions = ap
        realDoc.protect(spp)
        realDoc.save(docPath)
        realDoc.close()

    }
}