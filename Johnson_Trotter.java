import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 全排列生成
 */
public class Johnson_Trotter {
    // 窗体高度
    private static final int WINDOW_HEIGHT = 500;
    // 窗体宽度
    private static final int WINDOW_WIDTH = 600;


    /*
        1. 将所有元素初始化之后重复2-5步.
        2. 如果存在移动元素, 找到最大可移动元素K.
        3. 把K和它指向的元素互换.
        4. 反转比K大的元素的方向.
        5. 记录此时元素的排列
     */
    public static List<String> f(int i){
        List<NumBlock> numBlocks = new LinkedList<>();
        List<String> strings = new LinkedList<>();

        // 初始化
        intital(numBlocks, i);
        // 记录此时元素的排列
        strings.add(note(numBlocks));

        while (true) {
            try {
                // 得到可移动元素
                List<Integer> moves = getMoveAble(numBlocks);
                // 得到最大可移动元素K
                int K = getMax(numBlocks, moves);
                // 互换
                K = mySwap(numBlocks, K);
                // 反转比K大的元素的方向
                List<Integer> biggers = getBiggers(numBlocks, K);
                turnAll(numBlocks, biggers);
                // 记录此时元素的排列
                strings.add(note(numBlocks));
            } catch (Exception e) {
                return strings;
            }
        }
    }

    // 记录此时集合状态
    private static String note(List<NumBlock> numBlocks) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (int i = 0; i < numBlocks.size(); i++) {
            NumBlock numBlock = numBlocks.get(i);
            builder.append(numBlock.getNum());
            if (i < numBlocks.size() - 1) {
                builder.append(",");
            }
        }
        builder.append("]");
        return builder.toString();
    }

    // 反转所有
    private static void turnAll(List<NumBlock> numBlocks, List<Integer> biggers) {
        for (int index:biggers) {
            NumBlock numBlock = numBlocks.get(index);
            numBlock.turn();
        }
    }

    // 得到比k下标指示的元素还要大的元素下标集合
    private static List<Integer> getBiggers(List<NumBlock> numBlocks, int k) {
        List<Integer> integers = new LinkedList<>();
        NumBlock numBlock = numBlocks.get(k);
        for (int i = 0; i < numBlocks.size(); i++) {
            NumBlock numBlock1 = numBlocks.get(i);
            if (numBlock1.getNum() > numBlock.getNum()) {
                integers.add(i);
            }
        }
        return integers;
    }

    // 互换
    private static int mySwap(List<NumBlock> numBlocks, int k) {
        NumBlock numBlock = numBlocks.get(k);
        int i = k - ((numBlock.getDirection()==Direction.LEFT)? 1 : -1);
        Collections.swap(numBlocks, i, k);
        return i;
    }

    // 得到此集合中最大的可移动元素
    private static int getMax(List<NumBlock> numBlocks, List<Integer> moves) {
        int biggestIndex = -1;
        // 选取出可移动元素, 并比较
        for (int index:moves) {
            NumBlock numBlock = numBlocks.get(index);
            if (biggestIndex == -1) {
                biggestIndex = index;
                continue;
            }
            NumBlock numBlock1 = numBlocks.get(biggestIndex);
            if (numBlock.getNum() > numBlock1.getNum()) {
                biggestIndex = index;
            }
        }
        return biggestIndex;
    }

    // 得到此集合中可以移动的元素
    private static List<Integer> getMoveAble(List<NumBlock> numBlocks) {
        List<Integer> integers = new LinkedList<>();
        // 如果箭头指向一个相邻的较小元素(就是我指定的方向的相邻的元素比我小), 它在这个箭头标记的排列中是可移动的元素.
        for (int i = 0; i < numBlocks.size(); i++) {
            NumBlock block = numBlocks.get(i);
            NumBlock pointBlock;
            // 它指向的元素的下标
            int pointBlockIndex = i - ((block.getDirection()==Direction.LEFT)? 1 : -1);
            try {
                // 得到它指向的元素
                pointBlock = numBlocks.get(pointBlockIndex);
            } catch (Exception e) {
                continue;
            }
            // 程序到此处, 保证了元素block指向的pointBlock是存在的
            // 比较大小
            int blockNum = block.getNum();
            int pointBlockNum = pointBlock.getNum();
            if (blockNum > pointBlockNum) {
                integers.add(i);
            }
        }
        return integers;
    }

    // 初始化
    private static void intital(List<NumBlock> numBlocks, int i) {
        for (int j = 0; j < i; j++) {
            numBlocks.add(new NumBlock(j+1, Direction.LEFT));
        }
    }

    public static void main(String[] args) {
        // 构建窗体
        createWindow();
    }

    // 构建窗体
    private static void createWindow() {
        /*
        窗体包含
        1. 可编辑的输入框
        2. 确认按钮
        4. 输出结果框
        5. 等待动画(需要时显示)

        需要实现动作类的实现, 这里写一个内部类.
         */
        // 窗体
        JFrame window = new JFrame();
        window.setBounds(50, 50, WINDOW_WIDTH, WINDOW_HEIGHT);
        // 可编辑输入框
        JTextField textField = new JTextField();
        textField.setBounds(10, 10, WINDOW_WIDTH - 40, 30);
        // 输出结果框
        TextArea textArea = new TextArea();
        textArea.setBounds(10, 100, WINDOW_WIDTH - 40, WINDOW_HEIGHT - 150);
        // 等待动画
        Label label = new Label();
        label.setBounds(10, 50, 225, 50);
        label.setVisible(false);
        label.setFont(new Font("null", Font.BOLD, 18));
        label.setText(" ");
        Thread thread = new Thread(new MyThread("please Wait", label));
        thread.start();
        // 确认按钮
        JButton button = new JButton();
        button.setBounds(WINDOW_WIDTH - 180, 45, 150, 50);
        button.addActionListener(new MyAction(textField, textArea, label, window, null));
        button.setText("按下得到全列表");

        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setLayout(null);
        window.add(textField);
        window.add(button);
        window.add(textArea);
        window.add(label);
        window.setResizable(false);
        window.setVisible(true);
    }

    private static class MyAction implements ActionListener {
        JButton button;
        List<String> strings;
        JTextField textField;
        TextArea textArea;
        Label label;
        JFrame jFrame;

        public MyAction(JTextField textField, TextArea textArea, Label label, JFrame jFrame, JButton button) {
            this.textField = textField;
            this.textArea = textArea;
            this.label = label;
            this.jFrame = jFrame;
            this.button = button;
        }

        public void actionPerformed(ActionEvent e) {
            textArea.setFont(new Font("Consolas", Font.BOLD, 16));
            textArea.setText("");
            // 删除之前的生成log
            if (button != null) {
                jFrame.remove(button);
            }
            try {
            /*
                1. 根据输入得到输出, 打印在屏幕上
             */
                String userInput = textField.getText();
                int i = Integer.parseInt(userInput);
                if (i <= 0) throw new RuntimeException("错误");
                if (i >= 4) {
                    label.setVisible(true);
                    strings = f(i);
                    for (String s :
                            strings) {
                        textArea.append(s);
                        textArea.append("\n");
                    }
                    button = new JButton();
                    button.setBounds(275, 45, 150, 50);
                    button.setText("生成log");
                    button.addActionListener(new CreateAction(strings, label));
                    jFrame.add(button);

                } else {
                    StringBuilder builder = new StringBuilder();
                    strings = f(i);
                    for (String s :
                            strings) {
                        builder.append(s);
                        builder.append("\n");
                    }
                    textArea.append(builder.toString());
                }
                textArea.insert("一共有" + strings.size() + "种组合\n", 0);
                label.setVisible(false);

                MyAction action = (MyAction) button.getAction();
                action.strings = strings;

            } catch (NumberFormatException ex) {
                textArea.setText("错误!\n");
                textArea.append("输入的[");
                textArea.append(textField.getText());
                textArea.append("]不正确.\n");
                System.out.println("错误!\n输入的["+textField.getText()+"]不正确.\n");
            } catch (NullPointerException ignored) {
            } catch (RuntimeException ex) {
                textArea.setText(ex.getMessage());
            }
        }
    }

    private static class CreateAction implements ActionListener {
        List<String> strings;
        Label label;

        public List<String> getStrings() {
            return strings;
        }

        public void setStrings(List<String> strings) {
            this.strings = strings;
        }

        public Label getLabel() {
            return label;
        }

        public void setLabel(Label label) {
            this.label = label;
        }

        public CreateAction(List<String> strings, Label label) {
            this.strings = strings;
            this.label = label;
        }

        public void actionPerformed(ActionEvent e) {
            label.setVisible(true);
            File file = new File("");
            String path = file.getAbsoluteFile().getPath() + "\\log.txt";
            BufferedWriter writer;
            try {
                System.out.println("is creating!");

                FileWriter fw = new FileWriter(path);
                writer = new BufferedWriter(fw);
                writer.write("一共有"+strings.size()+"种组合.\n");
                for (String s:strings) {
                    writer.write(s + "\n");
                    writer.flush();
                }

                fw.close();
                writer.close();
                label.setVisible(false);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            System.out.println("log create! In["+file.getAbsoluteFile().getPath()+"\\log.txt"+"]");
        }
    }

    private static class MyThread extends Thread {
        Label label;
        boolean b = true;

        public MyThread(Label label) {
            this.label = label;
        }

        public MyThread(String name, Label label) {
            super(name);
            this.label = label;
        }

        public Label getLabel() {
            return label;
        }

        public void setLabel(Label label) {
            this.label = label;
        }

        public boolean isB() {
            return b;
        }

        public void setB(boolean b) {
            this.b = b;
        }

        public void close(){
            if (b) {
                b = false;
            }
        }

        public synchronized void start() {
            run();
        }

        public void run() {
            try {
                while (b) {
                    Thread.sleep(1000);
                    label.setText("please wait.");
                    Thread.sleep(1000);
                    label.setText("please wait..");
                    Thread.sleep(1000);
                    label.setText("please wait...");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

// 滑块
class NumBlock implements Comparable{
    private int num;
    private Direction direction;

    private int id;

    public void turn(){
        if (direction == Direction.LEFT) {
            direction = Direction.RIGHT;
        } else {
            direction = Direction.LEFT;
        }
    }

    public int compareTo(Object o) {
        if (o instanceof NumBlock){
            NumBlock numBlock = (NumBlock) o;
            return num - numBlock.getNum();
        }
        return 0;
    }

    public NumBlock(int num) {
        this.num = num;
    }
    public NumBlock(int num, Direction direction) {
        this.num = num;
        this.direction = direction;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NumBlock numBlock = (NumBlock) o;

        if (num != numBlock.num) return false;
        return getDirection() == numBlock.getDirection();
    }

    public int hashCode() {
        int result = num;
        result = 31 * result + (getDirection() != null ? getDirection().hashCode() : 0);
        return result;
    }

    public String toString() {
        return "["+direction+":"+num+"]";
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
// 方向
enum Direction{
    LEFT,RIGHT
}
