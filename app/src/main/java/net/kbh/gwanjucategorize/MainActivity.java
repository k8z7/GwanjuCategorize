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
        int type_01 = 0;
        int type_02 = 0;
        int type_03 = 0;
        int type_04 = 0;
        int type_05 = 0;
        int type_06 = 0;
        int type_07 = 0;
        int type_08 = 0;
        int type_09 = 0;
        int type_10 = 0;
        int type_out = 0;
        String outsider = "";

//========
// :: (2개)
//========
// :: (2개) // ':'로 끝남
        // 정규식 1) ^[0-9]+:~[0-9]+:$
        // 형태 '장:~장:'
        String regex_01 = "^[0-9]+:~[0-9]+:$";
        Pattern pattern_01 = Pattern.compile(regex_01);
// :: (2개) // '절'로 끝남
        // 정규식 2) ^[0-9]+:[0-9]+~[0-9]+:[0-9]+$
        // 형태 '장:절~장:절'
        String regex_02 = "^[0-9]+:[0-9]+~[0-9]+:[0-9]+$";
        Pattern pattern_02 = Pattern.compile(regex_02);
//========
// : (1개)
//========
// : (1개) // ~~ (2개)
        // 정규식 3) ^[0-9]+:[0-9]+~[0-9]+(,[0-9]+)*~[0-9]+$
        // 형태 '장:절~절(,절),절~절'
        String regex_03 = "^[0-9]+:[0-9]+~[0-9]+(,[0-9]+)*~[0-9]+$";
        Pattern pattern_03 = Pattern.compile(regex_03);
// : (1개) // ~ (1개) // '장'으로 시작
        // 정규식 4) ^[0-9]+:[0-9]+(,[0-9]+)*~([0-9]+,)*[0-9]+$
        // 형태 '장:절(,절)~(절,)절'
        String regex_04 = "^[0-9]+:[0-9]+(,[0-9]+)*~([0-9]+,)*[0-9]+$";
        Pattern pattern_04 = Pattern.compile(regex_04);
// : (1개) // ~ (1개) // '절'로 시작
        // 정규식 5) ^([0-9]+)~[0-9]+:[0-9]+$
        // 형태 '절~장:절'
        String regex_05 = "^([0-9]+)~[0-9]+:[0-9]+$";
        Pattern pattern_05 = Pattern.compile(regex_05);
// : (1개) // '~' 없음(0개) // ':'로 끝남
        // 정규식 6) ^[0-9]+:$
        // 형태 '장:'
        String regex_06 = "^[0-9]+:$";
        Pattern pattern_06 = Pattern.compile(regex_06);
// : (1개) // '~' 없음(0개) // '절'로 끝남
        // 정규식 7) ^[0-9]+:[0-9]+(,[0-9]+)*$
        // 형태 '장:절(,절)'
        String regex_07 = "^[0-9]+:[0-9]+(,[0-9]+)*$";
        Pattern pattern_07 = Pattern.compile(regex_07);
//========
// ':' 없음(0개)
//========
// ':' 없음(0개) // ~~ (2개)
        // 정규식 8) ^[0-9]+~.+~[0-9]+$
        // 형태 '절~절(,절),절~절'
        String regex_08 = "^[0-9]+~.+~[0-9]+$";
        Pattern pattern_08 = Pattern.compile(regex_08);
// ':' 없음(0개) // ~ (1개)
        // 정규식 9) ^[0-9]+(,[0-9]+)*~([0-9]+,)*[0-9]+$
        // 형태 '절(,절)~(절,)절'
        String regex_09 = "^[0-9]+(,[0-9]+)*~([0-9]+,)*[0-9]+$";
        Pattern pattern_09 = Pattern.compile(regex_09);
// ':' 없음(0개) // '~' 없음(0개)
        // 정규식 10) ^[0-9]+(,[0-9]+)*$
        // 형태 '절(,절)'
        String regex_10 = "^[0-9]+(,[0-9]+)*$";
        Pattern pattern_10 = Pattern.compile(regex_10);

        Matcher matcher;
        // 시간 스탬프
        long time = System.currentTimeMillis();
        for (String item:gwanjuAll) {
            matcher = pattern_01.matcher(item); if (matcher.find()) { type_01++; }
            else { matcher = pattern_02.matcher(item); if (matcher.find()) { type_02++; }
            else { matcher = pattern_03.matcher(item); if (matcher.find()) { type_03++; }
            else { matcher = pattern_04.matcher(item); if (matcher.find()) { type_04++; }
            else { matcher = pattern_05.matcher(item); if (matcher.find()) { type_05++; }
            else { matcher = pattern_06.matcher(item); if (matcher.find()) { type_06++; }
            else { matcher = pattern_07.matcher(item); if (matcher.find()) { type_07++; }
            else { matcher = pattern_08.matcher(item); if (matcher.find()) { type_08++; }
            else { matcher = pattern_09.matcher(item); if (matcher.find()) { type_09++; }
            else { matcher = pattern_10.matcher(item); if (matcher.find()) { type_10++; }
            else { type_out++; outsider += "\n" + item; // type 미상 자료 수집
            }}}}}}}}}}
            if ((System.currentTimeMillis() - time) > 1000) {
                Toast.makeText(this, "작업 중...", Toast.LENGTH_SHORT).show();
                time = System.currentTimeMillis();
            }
        }

        String outsiderFile = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "outsider.txt";
        // 5) 총수 = (분류 + 미분류) 여부 확인
        String rpt = "관주 장절표시 총수(unique) : " + TotalCount + " 개\n";
        rpt += ((type_01+type_02+type_03+type_04+type_05+type_06+type_07+type_08+type_09+type_10+type_out)==TotalCount) ? "<관주 숫자 이상무>" : "<관주 숫자 틀림>";
        rpt += "\n정규식 01) " + regex_01 + " = " + type_01;
        rpt += "\n정규식 02) " + regex_02 + " = " + type_02;
        rpt += "\n정규식 03) " + regex_03 + " = " + type_03;
        rpt += "\n정규식 04) " + regex_04 + " = " + type_04;
        rpt += "\n정규식 05) " + regex_05 + " = " + type_05;
        rpt += "\n정규식 06) " + regex_06 + " = " + type_06;
        rpt += "\n정규식 07) " + regex_07 + " = " + type_07;
        rpt += "\n정규식 08) " + regex_08 + " = " + type_08;
        rpt += "\n정규식 09) " + regex_09 + " = " + type_09;
        rpt += "\n정규식 10) " + regex_10 + " = " + type_10;
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