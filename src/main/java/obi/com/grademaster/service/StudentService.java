package obi.com.grademaster.service;


import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.DashedBorder;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import obi.com.grademaster.DTO.ScoreDto;
import obi.com.grademaster.DTO.StudentDto;
import obi.com.grademaster.entity.Student;
import obi.com.grademaster.exception.StudentNotFoundException;
import obi.com.grademaster.mapper.StudentMapper;
import obi.com.grademaster.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.RandomStringUtils;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    @Autowired
    StudentRepository studentRepository;
    @Autowired
    StudentMapper studentMapper;
    @Autowired
    ResourceLoaderService resourceLoaderService;



    public List<Student> getStudents() {
        return studentRepository.findAll();
    }

    public String generateRegNumber() {
        String randomDigits = RandomStringUtils.randomNumeric(6);
        return "2024%s3".formatted(randomDigits);
    }

    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findById(id);
    }

    public Optional<Student> getStudentByRegNumber(String regNumber) {
        return studentRepository.getStudentByRegNumber(regNumber);
    }

    public void saveStudent(Student student) {
        studentRepository.save(student);
    }

    public List<StudentDto> getStudentDtoList() {
        return studentRepository.findAll().stream().map(studentMapper::mapToStudentDto).toList();
    }

    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }

    public boolean isStudentExist(String regNumber) {
        return studentRepository.findAll()
                .stream()
                .anyMatch(student -> student.getRegNumber().equals(regNumber));
    }


    public ByteArrayOutputStream generatePdf(Long id, List<ScoreDto> scoreDtoList) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {

            Student student = getStudentById(id).orElseThrow(() -> new StudentNotFoundException("Student not found"));


            PdfWriter pdfWriter = new PdfWriter(baos);
            PdfDocument pdfDocument = new PdfDocument(pdfWriter);
            PageSize pageSize = PageSize.A4;
            pdfDocument.setDefaultPageSize(pageSize);

            Document document = new Document(pdfDocument);
            document.setTopMargin(1);

            Text studentName = new Text(student.getFirstName() + " " + student.getLastName())
                    .setFontColor(ColorConstants.DARK_GRAY)
                    .setFontSize(10);
            Text regNumber = new Text(student.getRegNumber())
                    .setFontColor(ColorConstants.DARK_GRAY)
                    .setFontSize(10);


            PdfFont pdfFont = PdfFontFactory.createFont(StandardFonts.TIMES_ITALIC);
            PdfFont pdfFont2 = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
            Text schoolName = new Text("Federal University of Nigeria")
                    .setFontSize(28)
                    .setFont(pdfFont)
                    .setFontColor(ColorConstants.DARK_GRAY);


            Paragraph paragraph = new Paragraph(schoolName);

            float[] columnWidths = {15f, 70f, 15f};
            Table divider = new Table(UnitValue.createPercentArray(1))
                    .useAllAvailableWidth()
                    .setBorder(new SolidBorder(ColorConstants.BLUE, 1f));
            Table divider2 = new Table(UnitValue.createPercentArray(1))
                    .useAllAvailableWidth()
                    .setBorder(new SolidBorder(ColorConstants.DARK_GRAY, 2f));
            Image image = resourceLoaderService.loadImage();
            image.setWidth(UnitValue.createPercentValue(100));


            Table headerTable = new Table(UnitValue.createPercentArray(columnWidths))
                    .useAllAvailableWidth()
                    .setBorder(Border.NO_BORDER)
                    .setHorizontalAlignment(HorizontalAlignment.CENTER)
                    .setMarginTop(3)
                    .setTextAlignment(TextAlignment.CENTER);


            Cell cell = new Cell().add(paragraph)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setBorder(Border.NO_BORDER);

            headerTable.addCell(new Cell().add(image).setBorder(Border.NO_BORDER));
            headerTable.addCell(cell);
            headerTable.addCell(new Cell().add(image).setBorder(Border.NO_BORDER));

            float[] twoColumn = new float[]{40f, 40f};
            Table studentDeteilsTable = new Table(twoColumn)
                    .setHorizontalAlignment(HorizontalAlignment.LEFT)
                    .setTextAlignment(TextAlignment.LEFT)
                    .setMarginTop(10f);

            studentDeteilsTable.addCell(new Cell()
                    .add(new Paragraph("Name:").setFontSize(10))
                    .setBorder(Border.NO_BORDER));
            studentDeteilsTable.addCell(new Cell()
                    .add(new Paragraph(studentName))
                    .setBorder(Border.NO_BORDER)
                    .setTextAlignment(TextAlignment.LEFT));
            studentDeteilsTable.addCell(new Cell()
                    .add(new Paragraph("Registration Number:").setFontSize(10))
                    .setBorder(Border.NO_BORDER));
            studentDeteilsTable.addCell(new Cell()
                    .add(new Paragraph(regNumber))
                    .setBorder(Border.NO_BORDER)
                    .setTextAlignment(TextAlignment.LEFT));


            Table resultDetails = new Table(UnitValue.createPercentArray(4))
                    .useAllAvailableWidth()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(12);

            resultDetails.addCell(new Cell()
                    .add(new Paragraph("Course Name"))
                    .setBorder(Border.NO_BORDER)
                    .setTextAlignment(TextAlignment.LEFT)
                    .setWidth(30f)
                    .setFont(pdfFont)
                    .setFontSize(16));
            resultDetails.addCell(new Cell()
                    .add(new Paragraph("Course Code"))
                    .setBorder(Border.NO_BORDER)
                    .setWidth(30f)
                    .setFont(pdfFont)
                    .setFontSize(16));

            resultDetails.addCell(new Cell()
                    .add(new Paragraph("Score"))
                    .setBorder(Border.NO_BORDER)
                    .setWidth(30f)
                    .setFont(pdfFont)
                    .setFontSize(16));
            resultDetails.addCell(new Cell()
                    .add(new Paragraph("Grade"))
                    .setBorder(Border.NO_BORDER)
                    .setWidth(30f)
                    .setFont(pdfFont)
                    .setFontSize(16));


            for (ScoreDto scoreDto : scoreDtoList) {
                resultDetails.addCell(new Cell()
                        .add(new Paragraph(scoreDto.getCourseName()))
                        .setBorder(Border.NO_BORDER)
                        .setTextAlignment(TextAlignment.LEFT)
                        .setWidth(30f)
                        .setFont(pdfFont2)
                        .setFontSize(14));

                resultDetails.addCell(new Cell()
                        .add(new Paragraph(scoreDto.getCourseCode()))
                        .setBorder(Border.NO_BORDER)
                        .setWidth(30f)
                        .setFont(pdfFont2)
                        .setFontSize(14));

                resultDetails.addCell(new Cell()
                        .add(new Paragraph(String.valueOf(scoreDto.getScore())))
                        .setBorder(Border.NO_BORDER)
                        .setWidth(30f)
                        .setFont(pdfFont2)
                        .setFontSize(14));

                resultDetails.addCell(new Cell()
                        .add(new Paragraph(String.valueOf(scoreDto.getGrade())))
                        .setBorder(Border.NO_BORDER)
                        .setWidth(30f)
                        .setFont(pdfFont2)
                        .setFontSize(14));
            }

            Table footer = new Table(UnitValue.createPercentArray(1))
                    .setHorizontalAlignment(HorizontalAlignment.CENTER)
                    .setBorder(new DashedBorder(ColorConstants.RED, 0.8f))
                    .useAllAvailableWidth();
            Text footerText = new Text("in pursuit of excellence")
                    .setFont(pdfFont)
                    .setFontColor(ColorConstants.BLACK);
            Paragraph footerParagraph = new Paragraph(footerText);
            document.setBackgroundColor(ColorConstants.DARK_GRAY);
            document.add(headerTable);
            document.add(divider);
            document.add(studentDeteilsTable);
            document.add(divider2);
            document.add(resultDetails);
            footerParagraph.setFixedPosition(pageSize.getWidth() / 2, pageSize.getBottom() + 20, pageSize.getWidth() - 40);
            footer.setFixedPosition(20, pageSize.getBottom() + 20, pageSize.getWidth() - 40);
            document.add(footerParagraph);
            document.add(footer);
            document.close();
            pdfWriter.close();


        } catch (IOException e) {

            throw new StudentNotFoundException("studentNotFound");
        }

        return baos;
    }

}

