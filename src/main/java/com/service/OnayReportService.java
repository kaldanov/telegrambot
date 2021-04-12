package com.service;

import com.config.Bot;
import com.entity.custom.Onay;
import com.repository.OnayRepository;
import com.repository.PropertiesRepository;
import com.repository.TelegramBotRepositoryProvider;
import com.util.Const;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OnayReportService {
    private Sheet sheets;
    private Sheet sheet;
    private XSSFWorkbook workbook = new XSSFWorkbook();
    private XSSFCellStyle style = workbook.createCellStyle();
    private List<Onay> onays;
    protected OnayRepository onayRepository = TelegramBotRepositoryProvider.getOnayRepository();
    protected PropertiesRepository propertiesRepository = TelegramBotRepositoryProvider.getPropertiesRepository();

    //public void sendOnayReport(long chatId, DefaultAbsSender bot, Date startDate, Date endDate) {
    public void sendOnayReport(long chatId, DefaultAbsSender bot) {
        try {
            //sendReport(chatId, bot, startDate, endDate);
            sendReport(chatId, bot);
        } catch (Exception e) {
            log.error("Can't create/send report", e);
            try {
                bot.execute(new SendMessage(chatId, "Ошибка при создании отчета"));
            } catch (TelegramApiException ex) {
                log.error("Can't send message", ex);
            }
        }
    }

    //private void sendReport(long chatId, DefaultAbsSender bot, Date startDate, Date endDate) throws TelegramApiException, IOException {
    private void sendReport(long chatId, DefaultAbsSender bot) throws TelegramApiException, IOException {

        sheets = workbook.createSheet("Отчет");
        sheet = workbook.getSheetAt(0);

        //onays = onayRepository.getAllByDateBetween(startDate, endDate);
        onays = onayRepository.findAll();

        if (onays.size() == 0 || onays == null) {
            bot.execute(new SendMessage(chatId, "Записи отсутствуют"));
        }
        BorderStyle thin = BorderStyle.THIN;
        short black = IndexedColors.BLACK.getIndex();
        XSSFCellStyle styleTitle = setStyle(workbook, thin, black, style);
        int rowIndex = 0;
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        SimpleDateFormat formatter1 = new SimpleDateFormat("dd.MM.yyyy");
        createTitle(styleTitle, rowIndex, Arrays.asList(("№;Дата поступления обращения;ФИО;ИИН;Номер телефона;Номер удост. личности;" +
                "Дата выдачи;Дата окон-я;Кем выдано;Ссылка на удост. личности;Ссылка на фото 3х4;").split(Const.SPLIT)));
        List<List<String>> onay = onays.stream().map(x -> {
            List<String> list = new ArrayList<>();
            list.add(String.valueOf(x.getId()));
            list.add(formatter.format(x.getDate()));
            list.add(x.getFullName());
            list.add(x.getIin());
            list.add(x.getPhone());
            list.add(x.getCardId());
            list.add(formatter1.format(x.getDateIssue()));
            list.add(formatter1.format(x.getDateEnd()));
            list.add(x.getIssuedBy());
            list.add("https://api.telegram.org/file/bot" + propertiesRepository.findById(Const.BOT_TOKEN).getValue() + "/" + uploadFile(x.getCardUrl(), bot));
            list.add("https://api.telegram.org/file/bot" + propertiesRepository.findById(Const.BOT_TOKEN).getValue() + "/" + uploadFile(x.getPhotoUrl(), bot));
            return list;
        }).collect(Collectors.toList());
        addInfo(onay, rowIndex);
        sendFile(chatId, bot);
    }

    private void sendFile(long chatId, DefaultAbsSender bot) throws IOException, TelegramApiException {
        String fileName = "Отчет.xlsx";
        String.format(fileName, new Date().getTime());
        String path = "C:\\botApps\\" + fileName;
        try (FileOutputStream stream = new FileOutputStream(path)) {
            workbook.write(stream);
        } catch (IOException e) {
            log.error("Can't send File error: ", e);
        }
        sendFile(chatId, bot, fileName, path);
    }

    private void sendFile(long chatId, DefaultAbsSender bot, String fileName, String path) throws IOException, TelegramApiException {
        File file = new File(path);
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            bot.execute(new SendDocument().setChatId(chatId).setDocument(fileName, fileInputStream));
        }
        file.delete();
    }

    private void addInfo(List<List<String>> reports, int rowIndex) {
        int cellIndex;
        for (List<String> report : reports) {
            sheets.createRow(++rowIndex);
            insertToRow(rowIndex, report, style);
        }
        for (int i = 1; i <= 11; i++) {
            sheets.autoSizeColumn(i);
        }
    }

    private void createTitle(XSSFCellStyle styleTitle, int rowIndex, List<String> title) {
        sheets.createRow(rowIndex);
        insertToRow(rowIndex, title, styleTitle);
    }

    private void insertToRow(int row, List<String> cellValues, CellStyle cellStyle) {
        int cellIndex = 0;
        for (String cellValue : cellValues) {
            addCellValue(row, cellIndex++, cellValue, cellStyle);
        }
    }

    private void addCellValue(int rowIndex, int cellIndex, String cellValue, CellStyle cellStyle) {
        sheets.getRow(rowIndex).createCell(cellIndex).setCellValue(getString(cellValue));
        sheet.getRow(rowIndex).getCell(cellIndex).setCellStyle(cellStyle);
    }

    private String getString(String nullable) {
        if (nullable == null) return "";
        return nullable;
    }

    private XSSFCellStyle setStyle(XSSFWorkbook workbook, BorderStyle thin, short black, XSSFCellStyle style) {
        style.setWrapText(true);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillBackgroundColor(IndexedColors.BLUE.getIndex());
        style.setBorderTop(thin);
        style.setBorderBottom(thin);
        style.setBorderRight(thin);
        style.setBorderLeft(thin);
        style.setTopBorderColor(black);
        style.setRightBorderColor(black);
        style.setBottomBorderColor(black);
        style.setLeftBorderColor(black);
        style.getFont().setBold(true);
        BorderStyle tittle = BorderStyle.MEDIUM;

        XSSFFont titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setColor(black);
        titleFont.setFontHeight(10);

        XSSFCellStyle styleTitle = workbook.createCellStyle();
        styleTitle.setWrapText(true);
        styleTitle.setAlignment(HorizontalAlignment.CENTER);
        styleTitle.setVerticalAlignment(VerticalAlignment.CENTER);
        styleTitle.setBorderTop(tittle);
        styleTitle.setBorderBottom(tittle);
        styleTitle.setBorderRight(tittle);
        styleTitle.setBorderLeft(tittle);
        styleTitle.setTopBorderColor(black);
        styleTitle.setRightBorderColor(black);
        styleTitle.setBottomBorderColor(black);
        styleTitle.setLeftBorderColor(black);
        styleTitle.setFont(titleFont);
        style.setFillForegroundColor(new XSSFColor(new Color(0, 94, 94)));
        return styleTitle;
    }

    private String uploadFile(String fileId, DefaultAbsSender bot) {
        //Bot bot = new Bot();
        //Objects.requireNonNull(fileId);
        GetFile getFile = new GetFile().setFileId(fileId);
        try {
            org.telegram.telegrambots.meta.api.objects.File file = bot.execute(getFile);
            return file.getFilePath();
        } catch (TelegramApiException e) {
            throw new IllegalMonitorStateException();
        }
    }

}
