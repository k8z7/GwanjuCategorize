package net.kbh.gwanjucategorize;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> bible_eng = new ArrayList<String>(Arrays.asList(new String[]{"chang", "chul", "re", "min", "sin", "su", "sas", "rus", "samsang", "samha", "wangsang", "wangha", "daesang", "daeha", "seu", "neu", "e", "yob", "si", "jam", "jun", "a", "sa", "rem", "ae", "gel", "dan", "ho", "yol", "am", "ob", "yon", "mi", "na", "hab", "seub", "hag", "seug", "mal", "ma", "mag", "nug", "yo", "haeng", "rom", "gojun", "gohu", "gal", "eb", "bil", "gol", "saljun", "salhu", "dimjun", "dimhu", "did", "mon", "hi", "yag", "bedjun", "bedhu", "yoil", "yoi", "yosam", "yu", "gye"}));
    private String rptFile = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "rpt.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (new File(rptFile).exists()) {
            categorizeGwanju();
        }
        else {
            uniqueGwanju();
        }
    }

    // 개역한글(국한문) 관주성경전서의 관주를 분류
    private void categorizeGwanju() {
        String text = readFile(rptFile);
        ArrayList<String> gwanjuAll = new ArrayList<String>(Arrays.asList(text.split("\n")));
        int TotalCount = gwanjuAll.size();
        int type_1 = 0;
        int type_2 = 0;
        int type_3 = 0;
        int type_4 = 0;
        int type_5 = 0;
        int type_6 = 0;
        int type_7 = 0;
        int type_8 = 0;
        int type_out = 0;
        String outsider = "";

        // 1) '장:~장:' - jangjul에 ':~' 포함된 종류
        // jangjul : 관주 숫자로 시작되는 어절
        String regex_1 = "^[0-9]{1,3}:~[0-9]{1,3}:$";
        Pattern pattern_1 = Pattern.compile(regex_1);
        // 2) '장:~장:절'
        String regex_2 = "^[0-9]{1,3}:~[0-9]{1,3}:[0-9]{1,3}$";
        Pattern pattern_2 = Pattern.compile(regex_2);
        // 3) '장:절~장:'
        String regex_3 = "^[0-9]{1,3}:[0-9]{1,3}~[0-9]{1,3}:$";
        Pattern pattern_3 = Pattern.compile(regex_3);
        // 4) '장:절~장:절'
        String regex_4 = "^[0-9]{1,3}:[0-9]{1,3}~[0-9]{1,3}:[0-9]{1,3}$";
        Pattern pattern_4 = Pattern.compile(regex_4);
        // 5) '절~장:절'
        String regex_5 = "^[0-9]{1,3}~[0-9]{1,3}:[0-9]{1,3}$";
        Pattern pattern_5 = Pattern.compile(regex_5);
        // 6) '장:(절,)절~절(,절)' // 단수장(본장)
        String regex_6 = "^([0-9]+):([0-9]+[,])*([0-9])+(~[0-9]+)*([,][0-9]+)*(~[0-9]+)*$";
        Pattern pattern_6 = Pattern.compile(regex_6);
        // 7) '장:'
        String regex_7 = "^[0-9]{1,3}:$";
        Pattern pattern_7 = Pattern.compile(regex_7);
        // 8) '(절,)절(,절)' // 단수장(본장)
        String regex_8 = "^([0-9]+[,]*[~]*)+([0-9][,]*)*$";
        Pattern pattern_8 = Pattern.compile(regex_8);

        Matcher matcher;
        // 시간 스탬프
        long time = System.currentTimeMillis();
        for (String item:gwanjuAll) {
            matcher = pattern_1.matcher(item); if (matcher.find()) { type_1++; }
            else { matcher = pattern_2.matcher(item); if (matcher.find()) { type_2++; }
            else { matcher = pattern_3.matcher(item); if (matcher.find()) { type_3++; }
            else { matcher = pattern_4.matcher(item); if (matcher.find()) { type_4++; }
            else { matcher = pattern_5.matcher(item); if (matcher.find()) { type_5++; }
            else { matcher = pattern_6.matcher(item); if (matcher.find()) { type_6++; }
            else { matcher = pattern_7.matcher(item); if (matcher.find()) { type_7++; }
            else { matcher = pattern_8.matcher(item); if (matcher.find()) { type_8++; }
            else { type_out++; outsider += "\n" + item; // type 미상 자료 수집
            }}}}}}}}
            if ((System.currentTimeMillis() - time) > 1000) {
                Toast.makeText(this, "작업 중...", Toast.LENGTH_SHORT).show();
                time = System.currentTimeMillis();
            }
        }

        String outsiderFile = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "outsider.txt";
        // 5) 총수 = (분류 + 미분류) 여부 확인
        String rpt = "관주 장절표시 총수(unique) : " + TotalCount + " 개\n";
        rpt += ((type_1+type_2+type_3+type_4+type_5+type_6+type_7+type_8+type_out)==TotalCount) ? "<관주 숫자 이상무>" : "<관주 숫자 틀림>";
        rpt += "\n" + regex_1 + " = " + type_1;
        rpt += "\n" + regex_2 + " = " + type_2;
        rpt += "\n" + regex_3 + " = " + type_3;
        rpt += "\n" + regex_4 + " = " + type_4;
        rpt += "\n" + regex_5 + " = " + type_5;
        rpt += "\n" + regex_6 + " = " + type_6;
        rpt += "\n" + regex_7 + " = " + type_7;
        rpt += "\n" + regex_8 + " = " + type_8;
        rpt += "\n" + "type_out = " + type_out;
        // 6) 보고서 출력
        writeFile(rpt+outsider, outsiderFile);
        // 7) 작업완료 토스팅
        Toast.makeText(this, "작업 완료", Toast.LENGTH_SHORT).show();
    }

    // 개역한글(국한문) 관주성경전서의 관주 장절표시를 중복제거하여 모두 수집
    private void uniqueGwanju() {
        // 1) 66권 차례로 unique 관주리스트 수집
        ArrayList<String> totalAL = new ArrayList<String>();
        for (String chag:bible_eng) {
            // srcText += readGwanju("bible" + File.separator + chag + ".txt").trim();
            totalAL.addAll(readGwanju("bible" + File.separator + chag + ".txt"));
        }
        // 2) 수집된 관주 중복제거
        totalAL = uniqueArrayList(totalAL); // ArrayList에서 unique한 데이터 추출
        // 3) 정렬
        Collections.sort(totalAL, ourComparator); // 보고서 만들기 전에 정렬
        // 4) 보고서 생성
        String srcText = "";
        for (String item:totalAL) srcText += "\n"+item;
        // 5) 보고서 출력
        writeFile(srcText.substring(1), rptFile);
        // 6) 작업완료 토스팅
        Toast.makeText(this, "작업 완료", Toast.LENGTH_SHORT).show();
    }

    // BufferedReader 파일읽기
    private String readFile(String path) {

        StringBuffer buffer = new StringBuffer();
        String str;

        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            String temp = br.readLine();
            if(temp != null) {
                buffer.append(temp);
                while ((temp = br.readLine()) != null) {
                    buffer.append("\n" + temp);
                }
            }
            br.close();

        } catch (FileNotFoundException e) {
            Toast.makeText(this, "FileNotFoundException", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "IOException", Toast.LENGTH_SHORT).show();
        }
        return buffer.toString();
    }

    // 정렬 도구(파라미터)
    private final Comparator ourComparator = new Comparator() {
        private final Collator collator = Collator.getInstance();
        @Override
        public int compare(Object object1, Object object2) {
            return collator.compare(object1.toString(), object2.toString());
        }
    };

    // BufferedOutputStream 파일쓰기
    private void writeFile(String fullTxt, String path) {
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(path));
            bos.write(fullTxt.getBytes());
            bos.close();
        } catch (FileNotFoundException e) {
            Toast.makeText(this, "FileNotFoundException", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "IOException", Toast.LENGTH_SHORT).show();
        }
    }

    // ArrayList에서 unique한 데이터 추출
    private ArrayList<String> uniqueArrayList(ArrayList<String> input_List) {
        ArrayList<String> result_List = new ArrayList<String>(); //결과를 담을 어레이리스트
        HashSet hs = new HashSet(input_List);
        Iterator it = hs.iterator();
        while(it.hasNext()){
            result_List.add(it.next().toString());
        }
        return result_List;
    }

    // assets 폴더의 성경 1권 읽어서 관주 추리고 중복제거
    private ArrayList<String> readGwanju(String file_name) {
        StringBuffer sb = new StringBuffer();
        BufferedReader br;
        ArrayList<String> chagAL = new ArrayList<String>();
        try {
            br = new BufferedReader(new InputStreamReader(getResources().getAssets().open(file_name)));
            String line = "";
            int p = -1;
            String regex = "^[0-9]";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher;
            while ((line = br.readLine()) != null) {
                // (1) 빈줄/난하주/장제목 제외
                if ((! line.isEmpty()) && (! line.startsWith("註")) && (line.indexOf(":") != -1)) {
                    // (2) 관주 있는 줄만 처리
                    p = line.indexOf("//");
                    if (p != -1) {
                        // (3) 관주 텍스트
                        line = line.substring(p+2).trim();
                        line = line.replaceAll("\\([?]\\)","");
                        String[] arr = line.split(" ");
                        // (4) 어절 단위
                        for (String item:arr) {
                            // (5) 숫자로 시작되는 것만 추림
                            matcher = pattern.matcher(item);
                            if (matcher.find()) {
                                chagAL.add(item);
                            }
                        }
                    }
                }
            }
            br.close();
        } catch (IOException e) {
            Toast.makeText(this, "IOException", Toast.LENGTH_SHORT).show();
        }

        return uniqueArrayList(chagAL);
    }

}