//package com.service;
//
//import com.entity.Properties;
//import com.entity.User;
//import baliviya.com.github.bodistrict.telegrambot.entity.custom.*;
//import com.entity.custom.*;
//import com.enums.Language;
//import baliviya.com.github.bodistrict.telegrambot.repository.*;
//import com.repository.*;
//import com.util.DateUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.poi.ss.usermodel.*;
//import org.apache.poi.ss.util.CellRangeAddress;
//import org.apache.poi.xssf.usermodel.XSSFCellStyle;
//import org.apache.poi.xssf.usermodel.XSSFFont;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.telegram.telegrambots.bots.DefaultAbsSender;
//import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
//import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
//import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
//
//import java.io.*;
//import java.text.SimpleDateFormat;
//import java.util.*;
//
//@Slf4j
//public class ServiceReportService {
//
//    long chatId;
//    private ServiceRepository serviceRepository = TelegramBotRepositoryProvider.getServiceRepository();
//    private PropertiesRepository propertiesRepository = TelegramBotRepositoryProvider.getPropertiesRepository();
//    private RegistrationServiceRepository registrationRepository = TelegramBotRepositoryProvider.getRegistrationServiceRepository();
//    private UserRepository userRepository = TelegramBotRepositoryProvider.getUserRepository();
//    private RecipientRepository recipientRepository = TelegramBotRepositoryProvider.getRecipientRepository();
//    private DirectionRepository directionRepository = TelegramBotRepositoryProvider.getDirectionRepository();
//    private DirectionRegistrationRepository directionRegistrationRepository = TelegramBotRepositoryProvider.getDirectionRegistrationRepository();
//    private ComesCourseRepository comesCourseRepository = TelegramBotRepositoryProvider.getComesCourseRepository();
//    private ReportServiceRepository reportServiceRepository = TelegramBotRepositoryProvider.getReportServiceRepository();
//    private MessageRepository messageRepository = TelegramBotRepositoryProvider.getMessageRepository();
//    private Properties properties;
//    private CategoriesIndicatorRepository categoriesIndicatorRepository = TelegramBotRepositoryProvider.getCategoriesIndicatorRepository();
//    private List<Category_Indicator> category_indicators;
//    private List<Registration_Service> registration_services;
//    private List<User> users;
//
//    private XSSFWorkbook workbook = new XSSFWorkbook();
//    private XSSFCellStyle style = workbook.createCellStyle();
//    private XSSFWorkbook originWorkbook;
//    private Sheet fifthOriginSheet;
//    private Sheet fifthSheet;
//    private Sheet secondOriginSheet;
//    private Sheet secondSheet;
//    private Date dateBegin;
//    private Date dateEnd;
//
//
//    public void sendServiceReport(long chatId, DefaultAbsSender bot, Date dateBegin, Date dateEnd, int messagePrevReport) throws TelegramApiException {
//        this.dateBegin = dateBegin;
//        this.dateEnd = dateEnd;
//        try {
//            this.chatId = chatId;
//            sendReport(chatId, bot, messagePrevReport);
//        } catch (Exception e) {
//            log.error("Can't create/send report", e);
//            try {
//                bot.execute(new SendMessage(chatId, "Ошибка при создании отчета"));
//            } catch (TelegramApiException ex) {
//                log.error("Can't send message", ex);
//            }
//        }
//    }
//
//    private void sendReport(long chatId, DefaultAbsSender bot, int messagePrevReport) throws IOException, TelegramApiException {
//
//        try {
//            properties = propertiesRepository.findById(4);
//            originWorkbook = new XSSFWorkbook(new FileInputStream(new File(properties.getValue())));
//        } catch (Exception e) {
//            log.error("Can't read file, error: ", e);
//        }
//        createSecondTitle();
//        addSecondPageInfo();
//        createFifthTitle();
//        addFifthPageInfo();
//        sendFile(chatId, bot, dateBegin, dateEnd);
//    }
//
//
//    private void createFifthTitle() {
//        fifthOriginSheet = originWorkbook.getSheetAt(1);
//        fifthSheet = workbook.createSheet(fifthOriginSheet.getSheetName());
//    }
//
//    private void createSecondTitle() {
//        secondOriginSheet = originWorkbook.getSheetAt(0);
//        secondSheet = workbook.createSheet(secondOriginSheet.getSheetName());
//    }
//
//    private void addSecondPageInfo() {
//        Row row = secondSheet.createRow(0);
//        Row row1 = secondSheet.createRow(1);
//        Row row2 = secondSheet.createRow(2);
//        Row row3, row4, row5;
//        Cell title;
//        secondSheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));
//        secondSheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 5));
//        secondSheet.setColumnWidth(1, 22000);
//        title = row.createCell(0);
//        title.setCellValue("ИНДИКАТОРЫ РАБОТЫ (" + DateUtil.getDayDate(dateBegin) + " - " + DateUtil.getDayDate(dateEnd) + ")");
//        title.setCellStyle(setStyleForCategories());
//        title = row1.createCell(0);
//        title.setCellValue(messageRepository.getMessageText(86, getLanguage().getId()));
//        title.setCellStyle(setStyleForIndicators(IndexedColors.YELLOW.getIndex()));
//
//        title = row2.createCell(0);
//        title.setCellValue("№");
//        title.setCellStyle(setStyleForIndicators(IndexedColors.GREEN.getIndex()));
//
//
//        title = row2.createCell(1);
//        title.setCellValue("НАИМЕНОВАНИЕ ИНДИКАТОРОВ");
//        title.setCellStyle(setStyleForIndicators(IndexedColors.GREEN.getIndex()));
//
//        title = row2.createCell(2);
//        title.setCellValue("ПЛАН \n (услуг)");
//        title.setCellStyle(setStyleForIndicators(IndexedColors.BRIGHT_GREEN.getIndex()));
//
//        title = row2.createCell(3);
//        title.setCellValue("ФАКТ \n (услуг)");
//        title.setCellStyle(setStyleForIndicators(IndexedColors.BRIGHT_GREEN.getIndex()));
//
//        title = row2.createCell(4);
//        title.setCellValue("ПЛАН \n (человек)");
//        title.setCellStyle(setStyleForIndicators(IndexedColors.LIGHT_BLUE.getIndex()));
//
//        title = row2.createCell(5);
//        title.setCellValue("ФАКТ \n (человек)");
//        title.setCellStyle(setStyleForIndicators(IndexedColors.LIGHT_BLUE.getIndex()));
//
//        List<Registration_Service> registration_services;
//
//        int indexIndicator = 1, indexService, rowIndex = 3;
//        StringBuilder stringIndex;
//        List<Service> services;
//        Set<String> userIinForDirection;
//        Set<String> userIinForIndicators;
//        Set<String> userIinForServices;
//        Set<String> totalUser = new HashSet<>();
//        int totalService = 0;
//        category_indicators = categoriesIndicatorRepository.findAllByLangIdOrderById(getLanguage().getId());
//
//        for (Category_Indicator category_indicator : category_indicators) {
//            int sizeOfCategory = 0;
//            userIinForIndicators = new HashSet<>();
//            row3 = secondSheet.createRow(rowIndex);
//            stringIndex = new StringBuilder(indexIndicator + ".");
//
//            title = row3.createCell(0);
//            title.setCellValue(stringIndex.toString());
//            title.setCellStyle(setStyleForIndicatorsForSecondPage(IndexedColors.LIGHT_YELLOW.getIndex()));
//
//            title = row3.createCell(1);
//            title.setCellValue(category_indicator.getName());
//            title.setCellStyle(setStyleForIndicatorsForSecondPage(IndexedColors.LIGHT_YELLOW.getIndex()));
//
//            rowIndex++;
//
//            services = serviceRepository.findAllByCategoryIdAndLangIdOrderById(category_indicator.getSecond(), getLanguage().getId());
//
//            indexService = 1;
//
//            for (Service service : services) {
//
//                registration_services = removeExcess(registrationRepository.findAllByServiceIdAndDateRegBetweenOrderById(service.getId2(), dateBegin, dateEnd));
//
//                userIinForServices = new HashSet<>();
//
//                stringIndex = new StringBuilder(indexIndicator + "." + indexService);
//
//                row4 = secondSheet.createRow(rowIndex);
//                title = row4.createCell(0);
//                title.setCellValue(stringIndex.toString());
//                title.setCellStyle(setStyleForSecondPage());
//
//                title = row4.createCell(1);
//                title.setCellValue(service.getName());
//                title.setCellStyle(setStyleForSecondPage());
//                for (Registration_Service registration_service : registration_services) {
//                    userIinForServices.add(registration_service.getIin());
//                }
//
//                title = row4.createCell(5);
//                title.setCellValue(userIinForServices.size());
//                title.setCellStyle(setStyleForSecondPage());
//
//                rowIndex++;
//
//                if (service.getStatusId() == 1) {
//                    List<Direction> directions = directionRepository.findAllByServiceIdOrderById(service.getId2());
//                    int sizeOfServices = 0;
//                    Map mapForDirection = new HashMap();
//                    for (Direction direction : directions) {
//                        userIinForDirection = new HashSet<>();
//                        int sizeOfDirections = 0;
//                        row5 = secondSheet.createRow(rowIndex);
//                        title = row5.createCell(1);
//                        mapForDirection.put(direction.getId(), rowIndex);
//                        if (getLanguage().getId() == 1)
//                            title.setCellValue(direction.getNameRus());
//                        else title.setCellValue(direction.getNameKaz());
//                        title.setCellStyle(setStyleForSecondPage());
//
//                        List<DirectionRegistration> directionRegistrations = directionRegistrationRepository.findAllByDirectionIdOrderById(direction.getId());
//                        List<Long> idOfReg = new ArrayList<>();
//                        if (directionRegistrations != null) {
//                            for (DirectionRegistration directionRegistration : directionRegistrations) {
//                                idOfReg.add(directionRegistration.getRegistrationId());
//                            }
//
//                            for (Registration_Service registration_service : registration_services) {
//                                if (idOfReg.contains(registration_service.getId())) {
//                                    sizeOfDirections += directionRegistrationRepository.findAllByRegistrationIdAndDirectionIdOrderById(registration_service.getId(), direction.getId()).size();
//                                    userIinForDirection.add(registration_service.getIin());
//                                    userIinForIndicators.add(registration_service.getIin());
//                                }
//                            }
//                        }
//
////                        userIinForDirection.add(registration_service.getIin());
////                        userIinForIndicators.add(registration_service.getIin());
//
//                        title = row5.createCell(3);
//                        title.setCellValue(sizeOfDirections);
//                        sizeOfServices += sizeOfDirections;
//                        sizeOfCategory += sizeOfDirections;
//                        title.setCellStyle(setStyleForSecondPage());
//                        title = row5.createCell(5);
//                        title.setCellValue(userIinForDirection.size());
//                        title.setCellStyle(setStyleForSecondPage());
//                        rowIndex++;
//                    }
//
//                    title = row4.createCell(3);
//                    title.setCellValue(sizeOfServices);
//                    title.setCellStyle(setStyleForSecondPage());
//
//                } else if (service.getStatusId() == 2) {
//                    int sizeOfCourses = 0;
//                    for (Registration_Service registration_service : registration_services) {
//                        sizeOfCourses += comesCourseRepository.findAllByRegistrationServiceIdAndActionDateBetweenOrderById(registration_service.getId(), dateBegin, dateEnd).size();
//                        userIinForIndicators.add(registration_service.getIin());
//                    }
//
//                    title = row4.createCell(3);
//                    title.setCellValue(sizeOfCourses);
//                    sizeOfCategory += sizeOfCourses;
//                    title.setCellStyle(setStyleForSecondPage());
//                } else if (service.getStatusId() == 3) {
//                    title = row4.createCell(3);
//                    title.setCellValue(reportServiceRepository.findAllByServiceId(service.getId2()).size());
//                    sizeOfCategory += reportServiceRepository.findAllByServiceId(service.getId2()).size();
//                    title.setCellStyle(setStyleForSecondPage());
//                    for (Registration_Service registration_service : registration_services) {
//                        userIinForIndicators.add(registration_service.getIin());
//                    }
//                }
//
//                indexService++;
//            }
//
//            title = row3.createCell(3);
//            title.setCellValue(sizeOfCategory);
//            totalService += sizeOfCategory;
//            title.setCellStyle(setStyleForIndicatorsForSecondPage(IndexedColors.LIGHT_YELLOW.getIndex()));
//
//            title = row3.createCell(5);
//            title.setCellValue(userIinForIndicators.size());
//            title.setCellStyle(setStyleForIndicatorsForSecondPage(IndexedColors.LIGHT_YELLOW.getIndex()));
//
//            indexIndicator++;
//
//        }
//
//
//        registration_services = removeExcess(registrationRepository.findAllByDateRegBetweenOrderById(dateBegin, dateEnd));
//
//        for (Registration_Service registration_service : registration_services) {
//            totalUser.add(registration_service.getIin());
//        }
//
//        row3 = secondSheet.createRow(rowIndex);
//
//        title = row3.createCell(0);
//        title.setCellStyle(setStyleForIndicators(IndexedColors.RED1.getIndex()));
//
//        title = row3.createCell(1);
//        title.setCellValue("Итого");
//        title.setCellStyle(setStyleForIndicators(IndexedColors.RED1.getIndex()));
//
//        title = row3.createCell(3);
//        title.setCellValue(totalService);
//        title.setCellStyle(setStyleForIndicators(IndexedColors.BRIGHT_GREEN.getIndex()));
//
//        title = row3.createCell(5);
//        title.setCellValue(totalUser.size());
//        title.setCellStyle(setStyleForIndicators(IndexedColors.LIGHT_BLUE.getIndex()));
//
//        for (int i = 0; i < 6; i++) {
//            if (i != 1) {
//                secondSheet.autoSizeColumn(i);
//            }
//        }
//    }
//
//    private void addFifthPageInfo() {
//        category_indicators = categoriesIndicatorRepository.findAllByLangIdOrderById(getLanguage().getId());
////        category_indicators = categoriesIndicatorRepository.findAllByLangId(getLanguage().getId());
//        Row row = fifthSheet.createRow(1);
//        Row row1 = fifthSheet.createRow(2);
//        row.setHeight((short) 1800);
//        int startRange = 7, endRange = 0;
//        Cell name;
//        int colorId = 0;
//        ArrayList<Short> color = new ArrayList<>();
//        color.add((short) 3);
//        color.add((short) 7);
//        color.add((short) 14);
//        color.add((short) 20);
//        color.add((short) 23);
//        color.add((short) 14);
//        color.add((short) 12);
//        color.add((short) 2);
//        color.add((short) 53);
//        color.add((short) 49);
//        color.add((short) 45);
//        color.add((short) 50);
//        color.add((short) 52);
//        color.add((short) 61);
//        color.add((short) 46);
//        color.add((short) 42);
//        color.add((short) 40);
//        color.add((short) 47);
//        color.add((short) 63);
//        color.add((short) 41);
//        Map map = new HashMap();
//        List<Service> services;
//        StringBuilder s;// = new StringBuilder();
//        List<Direction> directions;
//        for (Category_Indicator category_indicator : category_indicators) {
//            services = serviceRepository.findAllByCategoryIdAndLangIdOrderById(category_indicator.getSecond(), getLanguage().getId());
//            int size = services.size();
//            if (services.size() == 0) {
//                endRange = startRange + 1;
//            } else {
//                endRange = startRange + size * 2 - 1;
//            }
//            fifthSheet.addMergedRegion(new CellRangeAddress(1, 1, startRange, endRange));
//            name = row.createCell(startRange);
//            name.setCellStyle(setStyleForIndicators(color.get(colorId)));
//            int index = startRange - 1;
//            Cell name1;
//            for (Service service : services) {
//                index += 2;
//                name1 = row1.createCell(index);
//                s = new StringBuilder();
//                if (service.getStatusId() == 1) {
//                    directions = directionRepository.findAllByServiceIdOrderById(service.getId2());
//                    s.append(service.getName()).append("\n");
//                    for (Direction direction : directions) {
//                        if (getLanguage().getId() == 1)
//                            s.append(direction.getNameRus()).append("\n");
//                        else s.append(direction.getNameKaz()).append("\n");
//                    }
//                    name1.setCellValue(s.toString());
//                } else {
//                    name1.setCellValue(service.getName());
//                }
//                map.put(service.getId2(), index);
//                name1.setCellStyle(setStyleForIndicators(color.get(colorId)));
//            }
//            startRange = endRange + 1;
//            colorId++;
//            name.setCellValue(category_indicator.getName());
//        }
//        long difference = dateEnd.getTime() - dateBegin.getTime();
//        int days = (int) (difference / (24 * 60 * 60 * 1000));
//        int cellRangeForDate = endRange + 1;
//        Date startDate = dateBegin;
//        Calendar cal = Calendar.getInstance();
//        Cell cellForDateWeek, cellForDate;
//        SimpleDateFormat formatForDateWeekDays = new SimpleDateFormat("E");
//        SimpleDateFormat formatForDateDays = new SimpleDateFormat("dd.MM");
//        SimpleDateFormat formatForDateYears = new SimpleDateFormat("dd.MM.yyyy");
//        Map map2 = new HashMap();
//        for (int i = 0; i <= days; i++) {
//            cal.setTime(startDate);
//            cellForDateWeek = row.createCell(cellRangeForDate);
//            cellForDateWeek.setCellValue(formatForDateWeekDays.format(cal.getTime()));
//            cellForDate = row1.createCell(cellRangeForDate);
//            cellForDate.setCellValue(formatForDateDays.format(cal.getTime()));
//            if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
//                cellForDate.setCellStyle(setStyleForIndicators(IndexedColors.RED.getIndex()));
//                cellForDateWeek.setCellStyle(setStyleForIndicators(IndexedColors.RED.getIndex()));
//            }
//            map2.put(formatForDateYears.format(startDate.getTime()), cellRangeForDate);
//            cal.add(Calendar.DAY_OF_MONTH, 1);
//            startDate = cal.getTime();
//            cellRangeForDate++;
//        }
//
//        endRange = cellRangeForDate - 1;
//        name = row1.createCell(endRange + 1);
//        name.setCellValue("Келген күн саны");
//        name.setCellStyle(setStyleForIndicators(IndexedColors.LIGHT_GREEN.getIndex()));
//
//        name = row1.createCell(endRange + 2);
//        name.setCellValue("Алған қызмет саны");
//        name.setCellStyle(setStyleForIndicators(IndexedColors.BRIGHT_GREEN.getIndex()));
//
//        Cell index = row1.createCell(0);
//        index.setCellStyle(setStyleForIndicators(IndexedColors.YELLOW.getIndex()));
//        Cell fullName = row1.createCell(1);
//        fullName.setCellStyle(setStyleForIndicators(IndexedColors.YELLOW.getIndex()));
//        Cell iin = row1.createCell(2);
//        iin.setCellStyle(setStyleForIndicators(IndexedColors.YELLOW.getIndex()));
//        Cell number = row1.createCell(3);
//        number.setCellStyle(setStyleForIndicators(IndexedColors.YELLOW.getIndex()));
//        Cell status = row1.createCell(4);
//        status.setCellStyle(setStyleForIndicators(IndexedColors.YELLOW.getIndex()));
//        Cell disability = row1.createCell(5);
//        disability.setCellStyle(setStyleForIndicators(IndexedColors.YELLOW.getIndex()));
//        Cell district = row1.createCell(6);
//        district.setCellStyle(setStyleForIndicators(IndexedColors.YELLOW.getIndex()));
//
//        index.setCellValue("№");
//        fullName.setCellValue("Аты-жөні");
//        iin.setCellValue("ЖСН");
//        number.setCellValue("Байланыс телефоны");
//        status.setCellValue("Статус");
//        disability.setCellValue("Инвалидность");
//        district.setCellValue("Район");
//
//        registration_services = removeExcess(registrationRepository.findAllByDateRegBetweenOrderById(dateBegin, dateEnd));
//        Row row3;
//        int serviceStart = 3;
//        Recipient user;
//        Set<String> userIin = new HashSet<>();
//        for (Registration_Service registration_service : registration_services) {
//            userIin.add(registration_service.getIin());
//        }
//        Service service;
//        List<DirectionRegistration> directionRegistrations;
//        Direction direction;
//        Map map1;// = new HashMap();
//        Map map3;// = new HashMap();
//        Map map4 = new HashMap();
//        Map map7 = new HashMap();
//        int totalOfTheTotal = 0;
//        for (String iinOfUser : userIin) {
//            int total = 0;
//            user = recipientRepository.findByIin(String.valueOf(iinOfUser));
//            if (user != null) {
//                map1 = new HashMap();
//                map3 = new HashMap();
//                row3 = fifthSheet.createRow(serviceStart);
//                index = row3.createCell(0);
//                index.setCellStyle(setStyleForCategories());
//                fullName = row3.createCell(1);
//                fullName.setCellStyle(setStyleForCategories());
//                iin = row3.createCell(2);
//                iin.setCellStyle(setStyleForCategories());
//                number = row3.createCell(3);
//                number.setCellStyle(setStyleForCategories());
//                status = row3.createCell(4);
//                status.setCellStyle(setStyleForCategories());
//                disability = row3.createCell(5);
//                disability.setCellStyle(setStyleForCategories());
//                district = row3.createCell(6);
//                district.setCellStyle(setStyleForCategories());
//                index.setCellValue(user.getId());
//                fullName.setCellValue(user.getFullName());
//                iin.setCellValue(user.getIin());
//                number.setCellValue(user.getPhoneNumber());
//                status.setCellValue(user.getMaritalStatus());
//                disability.setCellValue(user.getDisability());
//                row3.setRowStyle(setStyleForCategories());
//                if (userRepository.findByChatId(user.getChatId()) != null) {
//                    district.setCellValue(userRepository.findByChatId(user.getChatId()).getEmail());
//                }
//                List<Registration_Service> registration_serviceList = removeExcess(registrationRepository.findAllByIinAndDateRegBetweenOrderById(iinOfUser, dateBegin, dateEnd));
//
//
//                //todo handling
//
//                Cell cellForReport;
//                int countCome = 0;
//                for (Registration_Service registration_service : registration_serviceList) {
//
//                    if (map.get(registration_service.getServiceId()) != null) {
//                        if (map7.get(formatForDateYears.format(registration_service.getDateReg())) == null) {
//                            Set set = new HashSet();
//                            set.add(registration_service.getIin());
//                            map7.put(formatForDateYears.format(registration_service.getDateReg()), set);
//                        } else {
//                            Set set;
//                            set = (Set) map7.get(formatForDateYears.format(registration_service.getDateReg()));
//                            set.add(registration_service.getIin());
//                            map7.put(formatForDateYears.format(registration_service.getDateReg()), set);
//                        }
//                        cellForReport = row3.createCell((Integer) map.get(registration_service.getServiceId()));
//                        service = serviceRepository.findById2AndLangId(registration_service.getServiceId(), getLanguage().getId());
//                        if (service.getStatusId() == 1) {
//                            String s1 = "";
//                            directionRegistrations = directionRegistrationRepository.findAllByRegistrationIdOrderById(registration_service.getId());
//                            if (map3.get(formatForDateYears.format(registration_service.getDateReg())) == null) {
//                                map3.put(formatForDateYears.format(registration_service.getDateReg()), directionRegistrations.size());
//                                if (map4.get(formatForDateYears.format(registration_service.getDateReg())) == null) {
//                                    map4.put(formatForDateYears.format(registration_service.getDateReg()), directionRegistrations.size());
//                                } else {
//                                    map4.put(formatForDateYears.format(registration_service.getDateReg()),
//                                            (Integer) map4.get(formatForDateYears.format(registration_service.getDateReg())) + directionRegistrations.size());
//                                }
//                            } else {
//                                map3.put(formatForDateYears.format(registration_service.getDateReg()),
//                                        (Integer) map3.get(formatForDateYears.format(registration_service.getDateReg())) + directionRegistrations.size());
//                                if (map4.get(formatForDateYears.format(registration_service.getDateReg())) == null) {
//                                    map4.put(formatForDateYears.format(registration_service.getDateReg()), directionRegistrations.size());
//                                } else {
//                                    map4.put(formatForDateYears.format(registration_service.getDateReg()),
//                                            (Integer) map4.get(formatForDateYears.format(registration_service.getDateReg())) + directionRegistrations.size());
//                                }
//                            }
//                            for (DirectionRegistration directionRegistration : directionRegistrations) {
//                                total++;
//                                direction = directionRepository.findById(directionRegistration.getDirectionId());
//                                if (getLanguage().getId() == 1)
//                                    s1 += direction.getNameRus() + "\n";
//                                else s1 += direction.getNameKaz() + "\n";
//                                if (map1.get(service.getId2()) == null)
//                                    map1.put(service.getId2(), 1);
//                                else map1.put(service.getId2(), (Integer) map1.get(service.getId2()) + 1);
//
//                            }
//                            cellForReport.setCellValue(s1);
//                            cellForReport.setCellStyle(setStyleForCategories());
//                        } else if (service.getStatusId() == 2) {
//                            cellForReport.setCellValue("+");
//                            cellForReport.setCellStyle(setStyleForCategories());
//                            List<ComesCourse> comesCourses = comesCourseRepository.findAllByRegistrationServiceIdAndActionDateBetweenOrderById(registration_service.getId(), dateBegin, dateEnd);
//                            if (map1.get(service.getId2()) == null)
//                                map1.put(service.getId2(), comesCourses.size());
//                            else
//                                map1.put(service.getId2(), (Integer) map1.get(service.getId2()) + comesCourses.size());
//                            for (ComesCourse comesCourse : comesCourses) {
//                                total++;
//                                if (map3.get(formatForDateYears.format(comesCourse.getActionDate())) == null) {
//                                    map3.put(formatForDateYears.format(comesCourse.getActionDate()), 1);
//                                    if (map4.get(formatForDateYears.format(registration_service.getDateReg())) == null) {
//                                        map4.put(formatForDateYears.format(registration_service.getDateReg()), 1);
//                                    } else {
//                                        map4.put(formatForDateYears.format(registration_service.getDateReg()),
//                                                (Integer) map4.get(formatForDateYears.format(registration_service.getDateReg())) + 1);
//                                    }
//                                } else {
//                                    map3.put(formatForDateYears.format(comesCourse.getActionDate()),
//                                            (Integer) map3.get(formatForDateYears.format(comesCourse.getActionDate())) + 1);
//                                    if (map4.get(formatForDateYears.format(registration_service.getDateReg())) == null) {
//                                        map4.put(formatForDateYears.format(registration_service.getDateReg()), 1);
//                                    } else {
//                                        map4.put(formatForDateYears.format(registration_service.getDateReg()),
//                                                (Integer) map4.get(formatForDateYears.format(registration_service.getDateReg())) + 1);
//                                    }
//                                }
//                            }
//                        } else if (service.getStatusId() == 3) {
//                            cellForReport.setCellValue("+");
//                            System.out.println(map1.get(service.getId2()));
//                            if (map1.get(service.getId2()) == null)
//                                map1.put(service.getId2(), 1);
//                            else
//                                map1.put(service.getId2(), (Integer) map1.get(service.getId2()) + 1);
//                            if (map3.get(formatForDateYears.format(registration_service.getDateReg())) == null) {
//                                map3.put(formatForDateYears.format(registration_service.getDateReg()), 1);
//                                if (map4.get(formatForDateYears.format(registration_service.getDateReg())) == null) {
//                                    map4.put(formatForDateYears.format(registration_service.getDateReg()), 1);
//                                } else {
//                                    map4.put(formatForDateYears.format(registration_service.getDateReg()),
//                                            (Integer) map4.get(formatForDateYears.format(registration_service.getDateReg())) + 1);
//                                }
//                            } else {
//                                map3.put(formatForDateYears.format(registration_service.getDateReg()),
//                                        (Integer) map3.get(formatForDateYears.format(registration_service.getDateReg())) + 1);
//                                if (map4.get(formatForDateYears.format(registration_service.getDateReg())) == null) {
//                                    map4.put(formatForDateYears.format(registration_service.getDateReg()), 1);
//                                } else {
//                                    map4.put(formatForDateYears.format(registration_service.getDateReg()),
//                                            (Integer) map4.get(formatForDateYears.format(registration_service.getDateReg())) + 1);
//                                }
//                            }
//                            total += (Integer) map1.get(service.getId2());
//                        }
//                        cellForReport = row3.createCell((Integer) map.get(registration_service.getServiceId()) - 1);
//                        if (map1.get(service.getId2()) != null)
//                            cellForReport.setCellValue((Integer) map1.get(service.getId2()));
//                        cellForReport.setCellStyle(setStyleForCategories());
//                    }
//
//                }
//
//
//                for (Object entryObj : map3.entrySet()) {
//                    Map.Entry entry = (Map.Entry) entryObj;
//                    Cell cellForReport3 = row3.createCell((Integer) map2.get(entry.getKey()));
//                    if (entry.getValue() != null) {
//                        cellForReport3.setCellValue((Integer) entry.getValue());
//                        cellForReport3.setCellStyle(setStyleForCategories());
//                    }
//                }
//
//                for (Object entryObj : map3.entrySet()) {
//                    Map.Entry entry = (Map.Entry) entryObj;
//                    Cell cellForReport3 = row3.createCell((Integer) map2.get(entry.getKey()));
//                    if (entry.getValue() != null) {
//                        cellForReport3.setCellValue((Integer) entry.getValue());
//                        cellForReport3.setCellStyle(setStyleForCategories());
//                    }
//                }
//
//                Cell cellForReport1;
//                cellForReport1 = row3.createCell(endRange + 1);
//                cellForReport1.setCellValue(map3.size());
//                cellForReport1.setCellStyle(setStyleForCategories());
//
//                cellForReport1 = row3.createCell(endRange + 2);
//                cellForReport1.setCellValue(total);
//                cellForReport1.setCellStyle(setStyleForCategories());
//
//                serviceStart++;
//
//                totalOfTheTotal += total;
//
//            }
//        }
//
//        Row row5 = fifthSheet.createRow(0);
//        Cell cellOfTheCell;
//        fifthSheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));
//        row5.setHeight((short) 1000);
//        cellOfTheCell = row5.createCell(0);
//        cellOfTheCell.setCellValue(messageRepository.getMessageText(86, getLanguage().getId()) + "\n" +
//                messageRepository.getMessageText(87, getLanguage().getId()) + " (" + DateUtil.getDayDate(dateBegin) + " - " + DateUtil.getDayDate(dateEnd) + ")");
//        cellOfTheCell.setCellStyle(setStyleForIndicators(IndexedColors.YELLOW.getIndex()));
//
//        Row row7 = fifthSheet.createRow(serviceStart);
//
//        Cell cellForReport4;
//
//        int totalUser = 0;
//
//        for (Object entryObj : map7.entrySet()) {
//            Map.Entry entry = (Map.Entry) entryObj;
//
//            cellForReport4 = row7.createCell((Integer) map2.get(entry.getKey()));
//            if (entry.getValue() != null) {
//                Set set = (Set) entry.getValue();
//                cellForReport4.setCellValue(set.size());
//                totalUser += set.size();
//                cellForReport4.setCellStyle(setStyleForCategories());
//                cellForReport4.setCellStyle(setStyleForIndicators(IndexedColors.BRIGHT_GREEN.getIndex()));
//            }
//        }
//
//        cellForReport4 = row7.createCell(1);
//        cellForReport4.setCellValue("Жалпы қамту (адам саны)");
//        cellForReport4.setCellStyle(setStyleForIndicators(IndexedColors.BRIGHT_GREEN.getIndex()));
//        cellForReport4 = row7.createCell(endRange + 1);
//        cellForReport4.setCellValue(totalUser);
//        cellForReport4.setCellStyle(setStyleForIndicators(IndexedColors.BRIGHT_GREEN.getIndex()));
//        row7.setRowStyle(setStyleForIndicators(IndexedColors.BRIGHT_GREEN.getIndex()));
//
//
//
//        Row row6 = fifthSheet.createRow(serviceStart + 1);
//
//        Cell cellForReport3;
//
//        for (Object entryObj : map4.entrySet()) {
//            Map.Entry entry = (Map.Entry) entryObj;
//            cellForReport3 = row6.createCell((Integer) map2.get(entry.getKey()));
//            if (entry.getValue() != null) {
//                cellForReport3.setCellValue((Integer) entry.getValue());
//                cellForReport3.setCellStyle(setStyleForCategories());
//                cellForReport3.setCellStyle(setStyleForIndicators(IndexedColors.LIGHT_YELLOW.getIndex()));
//            }
//        }
//
//        cellForReport3 = row6.createCell(1);
//        cellForReport3.setCellValue("Жалпы қызмет саны");
//        cellForReport3.setCellStyle(setStyleForIndicators(IndexedColors.LIGHT_YELLOW.getIndex()));
//        cellForReport3 = row6.createCell(endRange + 2);
//        cellForReport3.setCellValue(totalOfTheTotal);
//        cellForReport3.setCellStyle(setStyleForIndicators(IndexedColors.LIGHT_YELLOW.getIndex()));
//        row6.setRowStyle(setStyleForIndicators(IndexedColors.LIGHT_YELLOW.getIndex()));
//
////        cellForReport3.setCellStyle(setStyleForIndicators(IndexedColors.BRIGHT_GREEN.getIndex()));
//
//        for (int i = 0; i <= endRange; i++) {
//            fifthSheet.autoSizeColumn(i);
//        }
//    }
//
//    private List<Registration_Service> removeExcess(List<Registration_Service> registration_services) {
//        List<Registration_Service> registrationServiceList = registration_services;
//        Service service1;
//        for (int i = 0; i < registrationServiceList.size(); i++) {
//            service1 = serviceRepository.findById2AndLangId(registrationServiceList.get(i).getServiceId(), getLanguage().getId());
//            if (service1.getStatusId() == 1 || service1.getStatusId() == 3) {
//                if (!registrationServiceList.get(i).isFinish()) {
//                    registrationServiceList.remove(i);
//                }
//            }
//            if (service1.getStatusId() == 2) {
//                if (getCounComes(registrationServiceList.get(i)) == 0) {
//                    registrationServiceList.remove(i);
//                }
//            }
//        }
//        return registrationServiceList;
//    }
//
//    private int getCounComes(Registration_Service registration_service) {
//        return comesCourseRepository.findAllByRegistrationServiceIdOrderById(registration_service.getId()).size();
//    }
//
//    private XSSFCellStyle setStyleForIndicators(short color) {
//        XSSFCellStyle cellStyle = workbook.createCellStyle();
//        XSSFFont font = workbook.createFont();
//        BorderStyle title = BorderStyle.THIN;
//        cellStyle.setWrapText(true);
//        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//        cellStyle.setAlignment(HorizontalAlignment.CENTER);
//        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
//        cellStyle.setBorderTop(title);
//        cellStyle.setBorderBottom(title);
//        cellStyle.setBorderRight(title);
//        cellStyle.setBorderLeft(title);
//        cellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
//        cellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
//        cellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
//        cellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
//        cellStyle.setFillForegroundColor(color);
//        font.setFontName("Times New Roman");
//        font.setBold(true);
//        font.setFontHeight(14);
//        return cellStyle;
//    }
//
//    private XSSFCellStyle setStyleForIndicatorsForSecondPage(short color) {
//        XSSFCellStyle cellStyle = workbook.createCellStyle();
//        XSSFFont font = workbook.createFont();
//        BorderStyle title = BorderStyle.THIN;
//        cellStyle.setWrapText(true);
//        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//        cellStyle.setAlignment(HorizontalAlignment.LEFT);
//        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
//        cellStyle.setBorderTop(title);
//        cellStyle.setBorderBottom(title);
//        cellStyle.setBorderRight(title);
//        cellStyle.setBorderLeft(title);
//        cellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
//        cellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
//        cellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
//        cellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
//        cellStyle.setFillForegroundColor(color);
//        font.setFontName("Times New Roman");
//        font.setBold(true);
//        font.setFontHeight(14);
//        return cellStyle;
//    }
//
//    private XSSFCellStyle setStyleForCategories() {
//        XSSFCellStyle cellStyle = workbook.createCellStyle();
//        XSSFFont font = workbook.createFont();
//        BorderStyle title = BorderStyle.THIN;
//        cellStyle.setWrapText(true);
//        cellStyle.setFillBackgroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
//        cellStyle.setAlignment(HorizontalAlignment.CENTER);
//        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
//        cellStyle.setBorderTop(title);
//        cellStyle.setBorderBottom(title);
//        cellStyle.setBorderRight(title);
//        cellStyle.setBorderLeft(title);
//        cellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
//        cellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
//        cellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
//        cellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
//        cellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
//        font.setFontName("Times New Roman");
//        return cellStyle;
//    }
//
//    private XSSFCellStyle setStyleForSecondPage() {
//        XSSFCellStyle cellStyle = workbook.createCellStyle();
//        Font font = workbook.createFont();
//        BorderStyle title = BorderStyle.THIN;
//        cellStyle.setWrapText(true);
//        cellStyle.setAlignment(HorizontalAlignment.LEFT);
//        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
//        cellStyle.setBorderTop(title);
//        cellStyle.setBorderBottom(title);
//        cellStyle.setBorderRight(title);
//        cellStyle.setBorderLeft(title);
//        cellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
//        cellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
//        cellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
//        cellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
//        font.setFontName("Times New Roman");
//        return cellStyle;
//    }
//
//    private void sendFile(long chatId, DefaultAbsSender bot, Date dateBegin, Date dateEnd) throws IOException, TelegramApiException {
//        String fileName = "Болванка за: " + DateUtil.getDayDate(dateBegin) + " - " + DateUtil.getDayDate(dateEnd) + ".xlsx";
//        String path = "C:\\test\\" + fileName;
//        path += new Date().getTime();
//        try (FileOutputStream stream = new FileOutputStream(path)) {
//            workbook.write(stream);
//        } catch (IOException e) {
//            log.error("Can't send file error: ", e);
//        }
//        sendFile(chatId, bot, fileName, path);
//    }
//
//    private void sendFile(long chatId, DefaultAbsSender bot, String fileName, String path) throws TelegramApiException, IOException {
//        File file = new File(path);
//        try (FileInputStream fileInputStream = new FileInputStream(file)) {
//            bot.execute(new SendDocument().setChatId(chatId).setDocument(fileName, fileInputStream));
//        }
//        file.delete();
//    }
//
//    protected Language getLanguage() {
//        if (chatId == 0) return Language.ru;
//        return LanguageService.getLanguage(chatId);
//    }
//
//}