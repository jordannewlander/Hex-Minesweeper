import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Objects;

/**
 * @author jordan newlander
 * CS251L
 * HexMineGUI class
 */
//actionlistener library to include having clickable elements??
public class HexMineGUI implements ActionListener {
    static JFrame frame;
    static JButton[][] buttons;
    static JButton smallBoard;
    static JButton largeBoard;
    static JPanel board;
    static JPanel sidePanel;
    static JLabel flagLabel;
    static JLabel mineLabel;
    static JLabel timerLabel;
    static int ROWS;
    static int COLS;
    static int mines;
    static int flags = 0;
    static int timePlayed = 0;
    static HexMineManager game;
    static Timer timer;

    /**
     * HexMineGUI object initialization
     * holds all jframe components
     */
    public HexMineGUI(){
        frame = new JFrame("HexMineGUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        board = new JPanel(null);
        board.setPreferredSize(new Dimension(500, 500));
        board.setMinimumSize(new Dimension(400, 400));
        initBoard(board);
        frame.add(board, BorderLayout.CENTER);
        sidePanel = new JPanel();
        sidePanel.setMinimumSize(new Dimension(50, 400));
        sidePanel.setPreferredSize(new Dimension(100, 500));
        flagLabel = new JLabel("flags: " + flags);
        mineLabel = new JLabel("mines: " + mines);
        timerLabel = new JLabel("time played: " + timePlayed);
        smallBoard = new JButton("small board");
        smallBoard.setActionCommand("SMALL");
        smallBoard.addActionListener(this);
        largeBoard = new JButton("large board");
        largeBoard.setActionCommand("LARGE");
        largeBoard.addActionListener(this);
        sidePanel.add(flagLabel);
        sidePanel.add(mineLabel);
        sidePanel.add(timerLabel);
        sidePanel.add(smallBoard);
        sidePanel.add(largeBoard);
        frame.add(sidePanel, BorderLayout.EAST);
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * invokes runnable
     * to run GUI object
     * initialized in method
     */
    public static void showGUI(){
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                HexMineGUI gui = new HexMineGUI();
            }
        });
    }

    /**
     * starts the game:
     * initializes HexMineManager object
     * initializes HexMineManager game
     * shows GUI
     * starts timer
     */
    public static void startGame() {
        game = new HexMineManager(4, 4, 3);
        game.initGame();
        ROWS = game.rows;
        COLS = game.cols;
        mines = game.mineNum;
        buttons = new JButton[ROWS][COLS];
        showGUI();
        startTimer();
    }

    /**
     * starts gameTimer after game initialization
     */
    public static void startTimer() {
         int delay = 1000;

         ActionListener e = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timePlayed++;
                timerLabel.setText("time played: " + timePlayed);
            }
        };

        timer = new Timer(delay, e);

        timer.start();
    }

    /**
     * stops gameTimer after gameOver
     */
    public static void stopTimer(){
        timer.stop();
    }

    /**
     * creates message screen when game is over
     */
    public static void gameOver() {
        JOptionPane.showMessageDialog(null, "game over!!");
        stopTimer();
    }

    /**
     * sees if small button or large button is pressed
     * then updates GUI and HexMine board accordingly
     * @param e the event to be processed
     */
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals("SMALL"))  {
            HexMineManager.updateBoardSize(4, 4, 3);
            ROWS = 4;
            COLS = 4;
            mines = 3;
        }
        if (command.equals("LARGE")) {
            HexMineManager.updateBoardSize(6, 6, 4);
            ROWS = 6;
            COLS = 6;
            mines = 4;
        }
        refreshGUI();
    }

    /**
     * refreshes the GUI,
     * repaints the GUI,
     * reinitializes the GUI...
     */
    public void refreshGUI(){
        board.removeAll();
        buttons = new HexButton[ROWS][COLS];
        board.repaint();
        board.revalidate();
        initBoard(board);

        board.repaint();
        board.revalidate();
    }

    /**
     * initializes buttons based on offset coords
     * (so the hexagons are evenly spaced and all that jazz)
     * @param panel
     */
    public static void initBoard(JPanel panel) {
        int radius = 40;
        int width = radius * 2;
        int height = (int) Math.ceil(radius * Math.sqrt(3));

        for(int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                double x = col * (1.5 * radius);
                double y = row * (Math.sqrt(3) * radius);

                if (row % 2 != 0) x += 0.75 * radius;
                buttons[row][col] = new HexButton(row, col);
                buttons[row][col].setBounds((int)Math.round(x), (int) Math.round(y), width, height);
                buttons[row][col].setBackground(Color.LIGHT_GRAY);

                panel.add(buttons[row][col]);
            }
        }
    }

    /**
     * handles left click!
     * if click is not legal
     * shows all mines + calls gameover
     * else creates a list of points
     * storing the x and y intercepts of the cells cleared by
     * HexMineManager.findMineNum
     * and either shows how many mines the cells are adjacent to
     * or changes background color to white (empty)
     * repaints board
     * @param row
     * @param col
     */
    public static void handleLeftClick(int row, int col) {
        if (!Objects.equals(HexMineManager.board[row][col], HexMineManager.UNCLICKED)) return;
        if (!HexMineManager.isLegal(row, col)) {
            for (int r = 0; r < ROWS; r++) {
                for (int c = 0; c < COLS; c++) {
                    if (HexMineManager.board[r][c].equals(HexMineManager.MINE)) {
                        buttons[r][c].setText("M");
                        buttons[r][c].setForeground(Color.RED);
                    } else if (HexMineManager.findMineNum(r, c) > 0) {
                        buttons[r][c].setText(
                                "" + HexMineManager.findMineNum(r, c));
                        buttons[r][c].setForeground(Color.BLACK);
                    }
                }
            }
            gameOver();
            return;
        }
        List<Point> cleared = HexMineManager.clearedList(row, col);
        for (Point i : cleared) {
            int r = i.x;
            int c = i.y;
            if (HexMineManager.findMineNum(r, c) > 0) {
                buttons[r][c].setText(HexMineManager.findMineNum(r, c) + "");
                buttons[r][c].setForeground(Color.BLACK);
            } else {
                buttons[r][c].setText("");
                buttons[r][c].setBackground(Color.WHITE);
            }
        }
        board.repaint();
    }

    /**
     * handles  right click (flagging)
     * if cell  has not been flagged yet,
     * flags it, increases flag count, decreases mine count
     * and restates their labels accordingly
     * @param row
     * @param col
     */
    public static void handleRightClick(int row, int col){
        if (HexMineManager.board[row][col].equals(HexMineManager.FLAG)) {
            buttons[row][col].setText("");
            HexMineManager.board[row][col] = HexMineManager.UNCLICKED;

            if (flags > 0) flags--;
            if (mines <= HexMineManager.mineNum) mines++;
            flagLabel.setText("flags: " + flags);
            mineLabel.setText("mines: " + mines);
        } else if (HexMineManager.board[row][col].equals(HexMineManager.UNCLICKED)){
            HexMineManager.flagMine(row, col);
            buttons[row][col].setText("F");
            buttons[row][col].setForeground(Color.BLACK);

            flags++;
            if (mines > 0) mines--;
            flagLabel.setText("flags: " + flags);
            mineLabel.setText("mines: " + mines);
        }
    }

    /**
     * nested class creating custom hexagonal jbuttons
     */
    private static class HexButton extends JButton implements MouseListener {
        int radius = 40;
        int row;
        int col;

        /**
         * HexButton constructor
         * sets all jButton areas to false so can initialize
         * adds mouse listener that will differentiate
         * between left and right clicks
         * @param row
         * @param col
         */
        private HexButton(int row, int col){
            this.row = row;
            this.col = col;

            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);

            enableInputMethods(true);
            addMouseListener(this);
            setFocusable(true);

            addMouseListener(new MouseAdapter() {
                /**
                 * processes mouse events
                 * @param e the event to be processed
                 */
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        HexMineGUI.handleLeftClick(row, col);
                    } else if (SwingUtilities.isRightMouseButton(e)) {
                        HexMineGUI.handleRightClick(row, col);
                    }
                }
            });

        }

        /**
         * sets the bounds for the hexagon buttons
         * super from parent class (HexMineManager)
         * @param x the new <i>x</i>-coordinate of this component
         * @param y the new <i>y</i>-coordinate of this component
         * @param w the new {@code width} of this component
         * @param h the new {@code height} of this
         *          component
         */
        @Override
        public void setBounds(int x, int y, int w, int h){
            super.setBounds(x, y, w, h);
        }

        /**
         * all of these are empty because i didn't need them
         * but could not not initialize without it becoming an error
         * @param e the event to be processed
         */
        @Override
        public void mouseClicked(MouseEvent e) {}

        /**
         * empty again
         * @param e the event to be processed
         */
        @Override
        public void mousePressed(MouseEvent e){}

        /**
         * empty again
         * @param e the event to be processed
         */
        @Override
        public void mouseReleased(MouseEvent e){}

        /**
         * empty again
         * @param e the event to be processed
         */
        @Override
        public void mouseEntered(MouseEvent e) {}

        /**
         * empty... at last
         * @param e the event to be processed
         */
        @Override
        public void mouseExited(MouseEvent e) {}

        //these are all going to be equal to eachother
        //maybe experiment in the future??
        //make different size jpanels to host different
        //size gameboards??

        /**
         * gets the size of the ideal hexagon
         * @return dimensions of heagon
         */
        @Override
        public Dimension getPreferredSize(){
            int height = (int) (radius * Math.sqrt(3));
            int width = radius * 2;
            return new Dimension(width, height);
        }

        /**
         * this is equal to getPreferredSize
         * @return getPreferredSize
         */
        @Override
        public Dimension getMinimumSize(){
            return getPreferredSize();
        }

        /**
         * also same as getPreferredSize
         * @return getPreferredSize
         */
        @Override
        public Dimension getMaximumSize(){
            return getPreferredSize();
        }

        /**
         * creates polygon of points and fills it
         *
         * overrides paint component
         * @param g the <code>Graphics</code> object to protect
         */
        @Override
        public void paintComponent(Graphics g){
            g.setColor(getBackground());
            g.fillPolygon(createHex());
            super.paintComponent(g);
        }

        public Polygon createHex() {
            Polygon polygon = new Polygon();

            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;
            int x, y;
            for (int i = 0; i < 6; i++) {
                double angle = Math.toRadians(60 * i);
                x = (int) Math.round(centerX + (radius * Math.cos(angle)));
                y = (int) Math.round(centerY + (radius * Math.sin(angle)));
                polygon.addPoint(x, y);
            }// math.round called so int is rounded instead of truncated
            return polygon;
        }

        /**
         * sees if click is within the button
         * @param x   the <i>x</i> coordinate of the point
         * @param y   the <i>y</i> coordinate of the point
         * @return true if yes, false if no
         */
        @Override
        public boolean contains(int x, int y) {
            return createHex().contains(x, y);
        }
    }

    /**
     * starts the game
     */
    public static void main(String[] args) {
        startGame();
    }
}



