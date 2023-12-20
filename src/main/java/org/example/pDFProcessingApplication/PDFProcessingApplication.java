package org.example.pDFProcessingApplication;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;

public class PDFProcessingApplication {
    private static String signature;
    private static final int WORD_SIZE = 60;
    private static final int NUMBER_IMAGE_DIVIDED = 2;
    private static final int PERCENT_SIGNATURE_POSITION_LEFT = 20;
    private static final int PERCENT_SIGNATURE_POSITION_DOWN = 10;
    private static final int HUNDRED = 100;
    private static final float IMAGE_POSITION_BOTTOM = 10.0F;
    private static final float IMAGE_POSITION_LEFT = 10.0F;
    private static final String FONT_NAME = "Arial";
    private static final String FILE_NAME_POSTFIX = "_signed.pdf";
    private static final String FILE_EXTENSION = ".pdf";
    private static final String STAMP_IMAGE_PATH = "./stamps/stamp.png";
    private static final String IMAGE_PATH = Objects.requireNonNull(
            PDFProcessingApplication.class.getClassLoader().getResource(STAMP_IMAGE_PATH)).getPath();

    public static void main(String[] args) {
        start();
    }

    public static void start() {
        Scanner sc = new Scanner(System.in);
        boolean bool = true;
        while (bool) {
            System.out.println("please enter up to three lenght");
            signature = sc.nextLine();
            if (signature.length() <= 3 && !signature.isEmpty()) {
                bool = false;
            }
        }
        System.out.print("Enter the path of the PDF file: ");
        String pathPdf = sc.nextLine();
        addSignatureToPDF(pathPdf, signature);
        System.out.println("PDF successfully signed!");
    }

    private static void addSignatureToPDF(String pathPdf, String signature) {
        try (PDDocument document = Loader.loadPDF(new File(pathPdf))) {
            PDPageTree pdPageTree = document.getPages();
            for (PDPage pdPage : pdPageTree) {
                PDImageXObject image = LosslessFactory.createFromImage(document, createImage(signature));
                try (PDPageContentStream contentStream = new PDPageContentStream(document, pdPage, PDPageContentStream
                        .AppendMode.APPEND, true)) {
                    contentStream.drawImage(image, IMAGE_POSITION_LEFT, IMAGE_POSITION_BOTTOM);
                }
            }
            document.save(pathPdf.replace(FILE_EXTENSION, FILE_NAME_POSTFIX));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static BufferedImage createImage(String signature) {

        try {
            BufferedImage image = ImageIO.read(new FileImageInputStream(new File(IMAGE_PATH)));
            int signatureLeftPosition = image.getWidth() / NUMBER_IMAGE_DIVIDED - (image.getWidth() * PERCENT_SIGNATURE_POSITION_LEFT / HUNDRED);
            int signatureBottomPosition = image.getHeight() / NUMBER_IMAGE_DIVIDED + (image.getHeight() * PERCENT_SIGNATURE_POSITION_DOWN / HUNDRED);
            Graphics2D graphics2D = image.createGraphics();
            graphics2D.setColor(Color.BLACK);
            graphics2D.setFont(new Font(FONT_NAME, Font.BOLD, WORD_SIZE));
            graphics2D.drawString(signature, signatureLeftPosition, signatureBottomPosition);
            graphics2D.dispose();

            return image;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}

