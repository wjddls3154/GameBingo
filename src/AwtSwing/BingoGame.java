package AwtSwing;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Random;

public class BingoGame {

    // 필요한 기본 변수 선언
    static JPanel panelNorth; // 위쪽 패널, 기본 게임정보
    static JPanel panelCenter; // 가운데 패널, 게임화면
    static JLabel labelMessage;
    static JButton[] buttons = new JButton[16];
    static String[] images = {
      "fruit01.png","fruit02.png","fruit03.png","fruit04.png",
      "fruit05.png","fruit06.png","fruit07.png","fruit08.png",
      "fruit01.png","fruit02.png","fruit03.png","fruit04.png",
      "fruit05.png","fruit06.png","fruit07.png","fruit08.png"
    };

    static int openCount = 0; // opened Card Count, Index : 0, 1, 2, 안열었는지,하나만열었는지,둘다열었는지 체크
    static int buttonIndexSave1 = 0; // First Opened Card, Index 0~15 까지의 값을 가진다.
    static int buttonIndexSave2 = 0; // Second Opened Card, Index 0~15 까지의 값을 가진다.
    static Timer timer;
    static int tryCount = 0; // 몇번 시도했는지
    static int successCount = 0; // 빙고 카운트, 0~8

     static class MyFrame extends JFrame implements ActionListener { // 버튼 눌렀을때 액션을 받아오기위해 ActionListener

        // 생성자, Gui 구성
        public MyFrame(String title) {
            super(title);
            this.setLayout(new BorderLayout()); // 기본 프레임이 BorderLayout
            this.setSize(400,500);
            this.setVisible(true);
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            initUI(this); // 스크린 UI set
            mixCard(); // 카드 섞기

            this.pack(); // 여백이나 가장자리 정돈시켜 잡아준다.
        }


        // 버튼을 눌렀을때 일어나는 액션
        @Override
        public void actionPerformed(ActionEvent e) {
            if(openCount == 2) { // 카드가 두개 열렸으면, 더 열리지않게 한다.
                return;
            }

            // 실제로 눌러진 버튼
            JButton btn = (JButton)e.getSource();
            int index = getButtonIndex(btn);
            btn.setIcon(changeImage(images[index])); // 내가 눌린버튼에 이미지 이름이 찍혀서, 실제 이미지로 적용된다.

            openCount++;
            if(openCount == 1) { // 첫번째 카드인가 ? 그러면 저장
                buttonIndexSave1 = index;
            } else if(openCount == 2) { // 두번째 카드이면
                buttonIndexSave2 = index;
                tryCount++;
                labelMessage.setText("Find Same Fruit ! " + " Try : " + tryCount);

                // 판정 로직
                boolean isBingo = checkCard(buttonIndexSave1,buttonIndexSave2);
                if(isBingo == true) { // 빙고이면
                        openCount = 0;
                        successCount++;
                    buttons[buttonIndexSave1].setVisible(false);
                    buttons[buttonIndexSave2].setVisible(false);
                        if(successCount == 8) { // 8쌍을 모두 다 맞추면
                            labelMessage.setText("Game Clear Congratulation !! " + "Try : " + tryCount );
                        }
                } else {
                    backToQuestion();
                }

            }
        }

        // 빙고 맞추는데 실패하면, 다시 물음표 모양 카드로 돌아가도록 하는 메소드
        public void backToQuestion() {
            timer = new Timer(1000, new ActionListener() { // 카드 확인할수있도록, 1초 딜레이는 준다.
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Timer : ");

                    openCount = 0;
                    buttons[buttonIndexSave1].setIcon(changeImage("question.png"));
                    buttons[buttonIndexSave2].setIcon(changeImage("question.png"));
                    timer.stop();
                }
            });
            timer.start();
        }

        // 빙고 체크하는 메소드
        public boolean checkCard(int index1, int index2) {
            if(index1 == index2) {
                return false;
            }
            if(images[index1].equals(images[index2])) {
                return true;
            } else {
                return false;
            }
        }

        // 실제로 몇번째(index) 버튼이 눌렸는지 알아내는 메소드
        public int getButtonIndex(JButton btn){
            int index = 0;
            for (int i = 0; i < 16; i++) {
                if(buttons[i] == btn ) {    // Same instance ?
                    index = i;
                }
            }
            return  index;
        }

    }

    // 카드 섞는 메소드
    static void mixCard() {
        Random rand = new Random();
        for (int i = 0; i < 1000; i++) { // 1000번을 섞어준다.
            int random = rand.nextInt(15) + 1; // 1~15 까지의 값

            // 스왑 로직
            String temp = images[0];
            images[0] = images[random];
            images[random] = temp;
        }
    }

    // 스크린 화면 초기화 메소드
    static void initUI(MyFrame myFrame) {
        // panelNorth 설정
        panelNorth = new JPanel();
        panelNorth.setPreferredSize(new Dimension(400,100));
        panelNorth.setBackground(Color.BLUE);

        // labelMessage 설정
        labelMessage = new JLabel("Find Same Fruit !" + " Try 0");
        labelMessage.setPreferredSize(new Dimension(400,100));
        labelMessage.setForeground(Color.WHITE);
        labelMessage.setFont(new Font("Monaco",Font.BOLD,20));
        labelMessage.setHorizontalAlignment(JLabel.CENTER);

        panelNorth.add(labelMessage); // 만든 labelMessage 를 north 에 넣어준다.
        myFrame.add("North",panelNorth);

        // panelCenter (게임화면) 설정
        panelCenter = new JPanel();
        panelCenter.setLayout(new GridLayout(4,4));
        panelCenter.setPreferredSize(new Dimension(400,400));

        // 반복문, 과일 이미지와, 버튼이 16개라 16
        for (int i = 0; i < 16; i++) {
            buttons[i] = new JButton();
            buttons[i].setPreferredSize(new Dimension(100,100));
            buttons[i].setIcon(changeImage("question.png"));
            buttons[i].addActionListener(myFrame);
            panelCenter.add(buttons[i]);
        }
        myFrame.add("Center",panelCenter);
    }

    // 이미지의 이름을 주면, 스케일을 재조정해서 가져오는 메소드
    static ImageIcon changeImage(String filename) {
        ImageIcon icon = new ImageIcon("./" + filename); // ./ : 현재폴더
        Image originImage = icon.getImage();
        Image changedImage = originImage.getScaledInstance(100,100,Image.SCALE_SMOOTH);
        ImageIcon icon_new = new ImageIcon(changedImage);
        return icon_new;
    }


    public static void main(String[] args) {
        new MyFrame("Fruit Pair Bingo Game");

    }

}
