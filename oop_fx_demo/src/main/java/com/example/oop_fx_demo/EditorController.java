package com.example.oop_fx_demo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.dialog.FontSelectorDialog;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class EditorController {
    @FXML
    private TextArea areaText;
    private TextFile currentTextFile;
    @FXML
    private CheckMenuItem bold;
    @FXML
    private CheckMenuItem kyrs;
    private EditorModel model;

    boolean isSaveorSaveAs; //перебрасывание с сохранить на сохранить как
    boolean isSave; //сохранен ли файл

    public EditorController(EditorModel model){
        this.model = model;
    }

//ФАЙЛ
    //создать
    @FXML
    public void onNew() {
        String newStyle = "-fx-text-fill: black; -fx-control-inner-background: white;";
        if(isSave || areaText.getText().isEmpty()){
            areaText.clear();
            areaText.setFont(Font.font("Times New Roman",14));
            areaText.setStyle(newStyle);
            infoNew();
        }else {
            Alert alert = new Alert(
                    Alert.AlertType.WARNING,
                    "Вы хотите сохранить файл?",
                    ButtonType.YES,
                    ButtonType.NO
            );

            alert.setTitle("Файл");
            alert.setHeaderText("Файл не сохранен!");
            alert.showAndWait();

            if (alert.getResult() == ButtonType.YES) {
                onSave();
                areaText.clear();
                areaText.setFont(Font.font("Times New Roman",14));
                areaText.setStyle(newStyle);
                infoNew();
            }
            if (alert.getResult() == ButtonType.NO) {
                areaText.clear();
                areaText.setFont(Font.font("Times New Roman",14));
                areaText.setStyle(newStyle);
                infoNew();
            }
        }
        isSave = false;
        isSaveorSaveAs = false;
    }

    //окно "Файл создан"
    public void infoNew(){
        Alert alertNew = new Alert(Alert.AlertType.INFORMATION);
        alertNew.setHeaderText(null);
        alertNew.setTitle("Файл");
        alertNew.setContentText("Файл создан!");
        alertNew.show();
    }

    //открыть
    @FXML
    private void onOpen() throws IOException {
        if(isSave || areaText.getText().isEmpty()) {
            onOpenFile();
        }else {
            Alert alert = new Alert(
                    Alert.AlertType.WARNING,
                    "Вы хотите сохранить файл?",
                    ButtonType.YES,
                    ButtonType.NO
            );

            alert.setTitle("Файл");
            alert.setHeaderText("Файл не сохранен!");
            alert.showAndWait();

            if (alert.getResult() == ButtonType.YES) {
                onSave();
                onOpenFile();
            }
            if (alert.getResult() == ButtonType.NO) {
                onOpenFile();
            }
        }
        isSaveorSaveAs = true;
        isSave = false;
    }

    //само открытие
    public void onOpenFile() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text files (*.txt)", "*.txt")
        );
        fileChooser.setInitialDirectory(new File("./"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            IOResult<TextFile> io = model.load(file.toPath());

            if (io.isOk() && io.hasData()) {
                currentTextFile = io.getData();

                areaText.clear();
                currentTextFile.getContent().forEach(line -> areaText.appendText(line + "\n"));

                //считывание и применение стилей
                String sear = file.toPath().getFileName().toString();
                List<String> saveAllLinesForRewriting = new ArrayList<String>();
                BufferedReader bufferedReader = new BufferedReader(new FileReader("style.txt"));
                String saveLine;
                while ((saveLine = bufferedReader.readLine()) != null) {
                    saveAllLinesForRewriting.add(saveLine);
                }
                bufferedReader.close();

                if (saveAllLinesForRewriting.toString().contains(sear)) {
                    int ind = saveAllLinesForRewriting.indexOf(sear);
                    //установка стилей
                    String font_name = saveAllLinesForRewriting.get(ind + 1);
                    String t_st = saveAllLinesForRewriting.get(ind + 2);
                    String size_a = saveAllLinesForRewriting.get(ind + 3);
                    Double size = Double.parseDouble(size_a);
                    String st = saveAllLinesForRewriting.get(ind + 4);
                    String zh = saveAllLinesForRewriting.get(ind + 5);
                    if (st.contains("-fx-font-weight: bold;"))
                        bold.setSelected(true);
                    else
                        bold.setSelected(false);

                    if (st.contains("-fx-font-style: italic;"))
                        kyrs.setSelected(true);
                    else
                        kyrs.setSelected(false);

                    //установка шрифта
                    if (t_st.contains("Bold")) {
                        Font font = Font.font(font_name, FontWeight.BOLD, size);
                        areaText.setFont(font);
                        if (t_st.contains("Italic")) {
                            Font font1 = Font.font(font_name, FontWeight.BOLD, FontPosture.ITALIC, size);
                            areaText.setFont(font1);
                        }
                    } else if (t_st.contains("Italic")) {
                        Font font1 = Font.font(font_name, FontPosture.ITALIC, size);
                        areaText.setFont(font1);
                    } else {
                        Font font = Font.font(font_name, size);
                        areaText.setFont(font);
                    }

                    //установка цвета фона
                    String[] stylearea = st.split(";");
                    for (int i = 0; i < stylearea.length; i++) {
                        if (stylearea[i].contains("-fx-control-inner-background: ")) {
                            String back_arr = stylearea[i];
                            String back_color = back_arr.substring(30);
                            areaText.setStyle("-fx-control-inner-background: " + back_color + ";");
                        }
                    }

                    //установка цвета текста
                    String old_st1 = areaText.getStyle();
                    for (int x = 0; x < stylearea.length; x++) {
                        if (stylearea[x].contains("-fx-text-fill: ")) {
                            String text_arr = stylearea[x];
                            String text_color = text_arr.substring(15);
                            areaText.setStyle("-fx-text-fill: " + text_color + ";" + old_st1);
                        }
                    }
                } else {
                    System.out.println("Имя файла не найдено");
                }
            } else {
                System.out.println("Какая-то ошибка");
            }
        }

        //запись имени файла
        String fileName = "style.txt";
        try (FileWriter fw = new FileWriter(fileName, true)) {
            fw.write(file.toPath().getFileName() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        //удаление записи старых стилей
        String sear = file.toPath().getFileName().toString();
        List<String> saveAllLinesForRewriting = new ArrayList<String>();

        BufferedReader bufferedReader = new BufferedReader(new FileReader("style.txt"));
        String saveLine;
        while ((saveLine = bufferedReader.readLine()) != null) {
            saveAllLinesForRewriting.add(saveLine);
        }
        bufferedReader.close();

        if (saveAllLinesForRewriting.toString().contains(sear)) {
            int ind = saveAllLinesForRewriting.indexOf(sear);
            for (int i = ind; i < (ind + 7); i++) {
                removeNthLine("style.txt", ind);
            }
        } else {
            System.out.println("Не найдено");
        }
    }

    //метод для удаления всех стилей из файлика style
    public static void removeNthLine(String f, int toRemove) throws IOException {
        File tmp = File.createTempFile("tmp", "");

        BufferedReader br = new BufferedReader(new FileReader(f));
        BufferedWriter bw = new BufferedWriter(new FileWriter(tmp));

        for (int i = 0; i < toRemove; i++)
            bw.write(String.format("%s%n", br.readLine()));

        br.readLine();

        String l;
        while (null != (l = br.readLine()))
            bw.write(String.format("%s%n", l));

        br.close();
        bw.close();

        File oldFile = new File(f);
        if (oldFile.delete())
            tmp.renameTo(oldFile);

    }

    //сохранить
    @FXML
    private void onSave(){
        if (isSaveorSaveAs) {
            try {
                TextFile file = new TextFile(currentTextFile.getFile(), Arrays.asList(areaText.getText().split("\n")));
                model.save(file);
                infoSave();

                //получение информации о выбранном шрифте
                String fileName = "style.txt";
                String a = areaText.getFont().getFamily();
                String b = areaText.getFont().getStyle();
                Double size = areaText.getFont().getSize();
                String c = Double.toString(size);
                String d = areaText.getStyle();

                //запись стилей в файл
                try(FileWriter fw = new FileWriter(fileName, true))
                {
                    fw.write(a + "\n");
                    fw.write(b + "\n");
                    fw.write(c + "\n");
                    fw.write(d + "\n");
                    if(b == "Bold" || bold.isSelected()){
                        fw.write("-fx-font-weight: bold" + "\n" + "\n");
                    }else if(b == "Italic" || kyrs.isSelected()){
                        fw.write("-fx-font-style: italic;" + "\n" + "\n");
                    }else if(b == "Bold Italic"){
                        fw.write("-fx-font-weight: bold; -fx-font-style: italic;" + "\n" + "\n");
                    }else{
                        fw.write("-fx-font-weight: normal" + "\n" + "\n");
                    }
                    System.out.println("Данные успешно добавлены");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }catch (Exception e) {
                System.out.println("Error: " + e);
            }
        } else {
            onSaveAs();
        }

        isSaveorSaveAs = true;
        isSave = true;
    }

    //окно "Файл сохранен"
    public static void infoSave(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setTitle("Файл");
        alert.setContentText("Файл сохранён!");
        alert.show();
    }

    //сохранить как
    @FXML
    private void onSaveAs(){
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Text files (*.txt)", "*.txt")
            );
            fileChooser.setInitialDirectory(new File("./"));
            File file = fileChooser.showSaveDialog(null);

            if (file != null) {
                PrintWriter savedText = new PrintWriter(file);
                BufferedWriter out = new BufferedWriter(savedText);
                out.write(areaText.getText());
                out.close();
                infoSave();

                String fileName = "style.txt";
                //получение информации о выбранном шрифте
                String a = areaText.getFont().getFamily();
                String b = areaText.getFont().getStyle();
                Double size = areaText.getFont().getSize();
                String c = Double.toString(size);
                String d = areaText.getStyle();

                //запись стилей в файл
                try(FileWriter fw = new FileWriter(fileName, true))
                {
                    fw.write(file.toPath().getFileName() + "\n");
                    fw.write(a + "\n");
                    fw.write(b + "\n");
                    fw.write(c + "\n");
                    fw.write(d + "\n");
                    if(b == "Bold" || bold.isSelected()){
                        fw.write("-fx-font-weight: bold" + "\n" + "\n");
                    }else if(b == "Italic" || kyrs.isSelected()){
                        fw.write("-fx-font-style: italic;" + "\n" + "\n");
                    }else if(b == "Bold Italic"){
                        fw.write("-fx-font-weight: bold; -fx-font-style: italic;" + "\n" + "\n");
                    }else{
                        fw.write("-fx-font-weight: normal" + "\n" + "\n");
                    }
                    System.out.println("Данные успешно добавлены");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error: " + e);
        } catch (IOException e) {
            e.printStackTrace();
        }
        isSaveorSaveAs = true;
        isSave = true;
    }

    //закрыть
    @FXML
    private void onClose(){
        Alert alert = new Alert(
                Alert.AlertType.INFORMATION,
                "Вы точно хотите выйти?",
                ButtonType.YES,
                ButtonType.NO
        );
        alert.setTitle("Выход");
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            if (isSave) {
                model.close();
                return;
            } else {
                Alert alert1 = new Alert(
                        Alert.AlertType.INFORMATION,
                        "Вы хотите сохранить файл?",
                        ButtonType.YES,
                        ButtonType.NO
                );
                alert1.setTitle("");
                alert1.showAndWait();

                if (alert1.getResult() == ButtonType.YES) {
                    onSave();
                    model.close();
                    return;
                }
                if (alert1.getResult() == ButtonType.NO) {
                    model.close();
                    return;
                }
            }
        }
    }

//ПОМОЩЬ
    //о программе
    @FXML
    private void onAbout(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setTitle("О программе");
        alert.setContentText("Разработчик редактора Матюшанова Дарина П.");
        alert.show();
    }

//ПРАВКА
    //вырезать
    @FXML
    private void onCut(){areaText.cut();}

    //копировать
    @FXML
    private void onCopy(){areaText.copy();}

    //вставить
    @FXML
    private void onPaste(){areaText.paste();}

    //шрифт
    @FXML
    public void onFont(ActionEvent e) {
        FontSelectorDialog dialog = new FontSelectorDialog(null);
        dialog.setTitle("Font and Size");
        Optional<Font> response = dialog.showAndWait();
        if (response.isPresent()) {
            areaText.setFont(response.get());
        }
    }

    //цвет текста
    @FXML
    private void onColorText(){
        Stage windowcol = new Stage();
        windowcol.initModality(Modality.APPLICATION_MODAL);
        Scene scene = new Scene(new HBox(20), 400, 100);
        HBox box = (HBox) scene.getRoot();
        box.setPadding(new Insets(5, 5, 5, 5));
        final ColorPicker colorPicker = new ColorPicker();
        colorPicker.setValue(Color.CORAL);

        final Text text = new Text("Цветовая палитра!");
        text.setFont(Font.font ("Verdana", 20));
        text.setFill(colorPicker.getValue());

        colorPicker.setOnAction((ActionEvent e) -> {
            text.setFill(colorPicker.getValue());

            //установка цвета текста
            String stil = areaText.getStyle();
            String[] stylearea = stil.split(";");
            for(int x = 0; x<stylearea.length; x++) {
                if (stylearea[x].contains("-fx-text-fill:")) {
                    stylearea[x] = null;

                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < stylearea.length; i++) {
                        stringBuilder.append(stylearea[i] + ";");
                    }
                    String a = stringBuilder.toString();

                    String nl = "null;";
                    if(a.contains(nl)){
                        a = a.replace(nl, "");
                    }

                    String col = colorPicker.getValue().toString();
                    areaText.setStyle("-fx-text-fill: " + col.replace("0x", "#") + ";" + a);
                }
            }
            windowcol.close();
        });
        box.getChildren().addAll(colorPicker, text);

        windowcol.setScene(scene);
        windowcol.setTitle("ColorPicker");
        windowcol.showAndWait();
    }

    //цвет фона
    @FXML
    private void onColorFon(){
        Stage windowcol = new Stage();
        windowcol.initModality(Modality.APPLICATION_MODAL);
        Scene scene = new Scene(new HBox(20), 400, 100);
        HBox box = (HBox) scene.getRoot();
        box.setPadding(new Insets(5, 5, 5, 5));
        final ColorPicker colorPicker = new ColorPicker();
        colorPicker.setValue(Color.CORAL);

        final Text text = new Text("Цветовая палитра!");
        text.setFont(Font.font ("Verdana", 20));
        text.setFill(colorPicker.getValue());

        colorPicker.setOnAction((ActionEvent e) -> {
            text.setFill(colorPicker.getValue());

            //установка цвета фона
            String stil = areaText.getStyle();
            String[] stylearea = stil.split(";");
            for(int x = 0; x<stylearea.length; x++) {
                if (stylearea[x].contains("-fx-control-inner-background:")) {
                    stylearea[x] = null;

                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < stylearea.length; i++) {
                        stringBuilder.append(stylearea[i] + ";");
                    }
                    String a = stringBuilder.toString();

                    String nl = "null;";
                    if(a.contains(nl)){
                        a = a.replace(nl, "");
                    }

                    String col = colorPicker.getValue().toString();
                    areaText.setStyle("-fx-control-inner-background: " + col.replace("0x", "#") + ";" + a);

                }
            }
            windowcol.close();
        });
        box.getChildren().addAll(colorPicker, text);

        windowcol.setScene(scene);
        windowcol.setTitle("ColorPicker");
        windowcol.showAndWait();
    }

    @FXML
    private void onDataTime(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        areaText.appendText(dateFormat.format(cal.getTime()));
    }

//Стиль
    //жирное начертание
    @FXML
    private void onBold() {
        if(bold.isSelected()) {
            String b = areaText.getFont().getStyle();

            if(b.contains("Bold")){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText(null);
                alert.setTitle("Начертание");
                alert.setContentText("Текст уже имеет жирное начертание!");
                alert.show();

                bold.setSelected(false);
            }else {
                String style = areaText.getStyle();
                if(!style.contains("-fx-font-weight:")){
                    areaText.setStyle("-fx-font-weight: normal;" + style);

                    String style2 = areaText.getStyle();
                    int n = 66;
                    String st = getLastN(style2, n);
                    if (style2.contains("-fx-font-style:")) {
                        String it = "-fx-font-style: italic;";
                        areaText.setStyle(it + "-fx-font-weight: bold;" + st);
                    } else {
                        areaText.setStyle("-fx-font-weight: bold;" + st);
                    }
                }else {
                    String style2 = areaText.getStyle();
                    int n = 65;
                    String st = getLastN(style2, n);
                    if (style2.contains("-fx-font-style:")) {
                        String it = "-fx-font-style: italic;";
                        areaText.setStyle(it + "-fx-font-weight: bold;" + st);
                    } else {
                        areaText.setStyle("-fx-font-weight: bold;" + st);
                    }
                }
            }
        }
        else {
            String style2 = areaText.getStyle();
            System.out.println(style2);
            int n = 65;
            String st = getLastN(style2, n);
            if(style2.contains("-fx-font-style:")){
                String it = "-fx-font-style: italic;";
                areaText.setStyle(it + "-fx-font-weight: normal;" + st);
            }else{
                areaText.setStyle("-fx-font-weight: normal;" + st);
            }
        }
    }

    //курсив
    @FXML
    private void onKyrs() {
        if(kyrs.isSelected()) {
            String b = areaText.getFont().getStyle();

            if(b.contains("Italic")){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText(null);
                alert.setTitle("Начертание");
                alert.setContentText("Текст уже имеет курсивное начертание!");
                alert.show();

                kyrs.setSelected(false);
            }else {
                String style2 = areaText.getStyle();
                int n = 65;
                String st = getLastN(style2, n);
                if(style2.contains("-fx-font-weight: normal")){
                    String nor = "-fx-font-weight: normal;";
                    areaText.setStyle(nor + "-fx-font-style: italic;" + st);
                }else if(style2.contains("-fx-font-weight: bold")){
                    String bol = "-fx-font-weight: bold;";
                    areaText.setStyle(bol + "-fx-font-style: italic;" + st);
                }
            }
        }
        else {
            String style2 = areaText.getStyle();
            int n = 65;
            String st = getLastN(style2, n);
            if(style2.contains("-fx-font-weight: normal")){
                String nor = "-fx-font-weight: normal;";
                areaText.setStyle(nor + st);
            }else if(style2.contains("-fx-font-weight: bold")){
                String bol = "-fx-font-weight: bold;";
                areaText.setStyle(bol + st);
            }
        }
    }

    //метод для получения последних символов строки
    public static String getLastN(String s, int n) {
        if (s == null || n > s.length()) {
            return s;
        }
        return s.substring(s.length() - n);
    }
}