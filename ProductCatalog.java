import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.*;

/* ================================================================
   TASK 1 -- Product Class
   ================================================================ */
class Product {
    private int productId; private String name,category,description,brand;
    private double price,rating; private int stock;
    public Product(int id,String n,String cat,double price,String desc,String brand,double rating,int stock){
        this.productId=id;this.name=n;this.category=cat;this.price=price;
        this.description=desc;this.brand=brand;this.rating=rating;this.stock=stock;
    }
    public int getProductId(){return productId;} public String getName(){return name;}
    public String getCategory(){return category;} public double getPrice(){return price;}
    public String getDescription(){return description;} public String getBrand(){return brand;}
    public double getRating(){return rating;} public int getStock(){return stock;}
}
/* ================================================================
   TASK 3 -- Comparators
   ================================================================ */
class PriceComparator implements Comparator<Product>{
    private final boolean asc;
    public PriceComparator(boolean asc){this.asc=asc;}
    @Override public int compare(Product a,Product b){
        return asc?Double.compare(a.getPrice(),b.getPrice()):Double.compare(b.getPrice(),a.getPrice());
    }
}
class NameComparator implements Comparator<Product>{
    private final boolean asc;
    public NameComparator(boolean asc){this.asc=asc;}
    @Override public int compare(Product a,Product b){
        return asc?a.getName().compareToIgnoreCase(b.getName()):b.getName().compareToIgnoreCase(a.getName());
    }
}
/* ================================================================
   DATABASE MANAGER -- SQLite
   ================================================================ */
class DatabaseManager{
    private static final String DB_URL="jdbc:sqlite:shopswing.db";
    private Connection conn;
    public DatabaseManager(){
        try{
            Class.forName("org.sqlite.JDBC");
            conn=DriverManager.getConnection(DB_URL);
            createTables();
        }catch(Exception e){
            JOptionPane.showMessageDialog(null,
                "DB Error: "+e.getMessage()+"\nDownload sqlite-jdbc.jar and add to classpath.\nhttps://github.com/xerial/sqlite-jdbc/releases",
                "Database Error",JOptionPane.ERROR_MESSAGE);
        }
    }
    private void createTables()throws SQLException{
        Statement st=conn.createStatement();
        st.execute("CREATE TABLE IF NOT EXISTS users(id INTEGER PRIMARY KEY AUTOINCREMENT,username TEXT UNIQUE NOT NULL,email TEXT UNIQUE NOT NULL,password TEXT NOT NULL,created_at TEXT NOT NULL)");
        st.execute("CREATE TABLE IF NOT EXISTS cart(id INTEGER PRIMARY KEY AUTOINCREMENT,user_id INTEGER NOT NULL,product_id INTEGER NOT NULL,quantity INTEGER NOT NULL DEFAULT 1,added_at TEXT NOT NULL,UNIQUE(user_id,product_id))");
        st.execute("CREATE TABLE IF NOT EXISTS wishlist(id INTEGER PRIMARY KEY AUTOINCREMENT,user_id INTEGER NOT NULL,product_id INTEGER NOT NULL,added_at TEXT NOT NULL,UNIQUE(user_id,product_id))");
        st.execute("CREATE TABLE IF NOT EXISTS orders(id INTEGER PRIMARY KEY AUTOINCREMENT,user_id INTEGER NOT NULL,total_amount REAL NOT NULL,status TEXT NOT NULL DEFAULT 'Placed',created_at TEXT NOT NULL)");
        st.execute("CREATE TABLE IF NOT EXISTS order_items(id INTEGER PRIMARY KEY AUTOINCREMENT,order_id INTEGER NOT NULL,product_id INTEGER NOT NULL,product_name TEXT NOT NULL,price REAL NOT NULL,quantity INTEGER NOT NULL)");
        st.close();
    }
    private String now(){return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));}

    public int registerUser(String u,String e,String p){
        try{
            PreparedStatement ps=conn.prepareStatement("INSERT INTO users(username,email,password,created_at) VALUES(?,?,?,?)");
            ps.setString(1,u);ps.setString(2,e);ps.setString(3,p);ps.setString(4,now());ps.executeUpdate();
            ResultSet rs=conn.createStatement().executeQuery("SELECT last_insert_rowid()");
            return rs.next()?rs.getInt(1):-1;
        }catch(SQLException ex){return -1;}
    }
    public int[] loginUser(String u,String p){
        try{
            PreparedStatement ps=conn.prepareStatement("SELECT id FROM users WHERE username=? AND password=?");
            ps.setString(1,u);ps.setString(2,p);ResultSet rs=ps.executeQuery();
            if(rs.next())return new int[]{rs.getInt("id"),1};
        }catch(SQLException ignored){}
        return new int[]{-1,0};
    }
    public String[] getUserInfo(int uid){
        try{
            PreparedStatement ps=conn.prepareStatement("SELECT username,email,created_at FROM users WHERE id=?");
            ps.setInt(1,uid);ResultSet rs=ps.executeQuery();
            if(rs.next())return new String[]{rs.getString("username"),rs.getString("email"),rs.getString("created_at")};
        }catch(SQLException ignored){}
        return new String[]{"Unknown","",""};
    }
    public void addToCart(int uid,int pid){
        try{
            PreparedStatement ps=conn.prepareStatement("INSERT INTO cart(user_id,product_id,quantity,added_at) VALUES(?,?,1,?) ON CONFLICT(user_id,product_id) DO UPDATE SET quantity=quantity+1");
            ps.setInt(1,uid);ps.setInt(2,pid);ps.setString(3,now());ps.executeUpdate();
        }catch(SQLException ignored){}
    }
    public void removeFromCart(int uid,int pid){
        try{PreparedStatement ps=conn.prepareStatement("DELETE FROM cart WHERE user_id=? AND product_id=?");ps.setInt(1,uid);ps.setInt(2,pid);ps.executeUpdate();}catch(SQLException ignored){}
    }
    public void updateCartQty(int uid,int pid,int qty){
        if(qty<=0){removeFromCart(uid,pid);return;}
        try{PreparedStatement ps=conn.prepareStatement("UPDATE cart SET quantity=? WHERE user_id=? AND product_id=?");ps.setInt(1,qty);ps.setInt(2,uid);ps.setInt(3,pid);ps.executeUpdate();}catch(SQLException ignored){}
    }
    public List<int[]> getCart(int uid){
        List<int[]> list=new ArrayList<>();
        try{PreparedStatement ps=conn.prepareStatement("SELECT product_id,quantity FROM cart WHERE user_id=? ORDER BY added_at");ps.setInt(1,uid);ResultSet rs=ps.executeQuery();while(rs.next())list.add(new int[]{rs.getInt("product_id"),rs.getInt("quantity")});}catch(SQLException ignored){}
        return list;
    }
    public void clearCart(int uid){
        try{PreparedStatement ps=conn.prepareStatement("DELETE FROM cart WHERE user_id=?");ps.setInt(1,uid);ps.executeUpdate();}catch(SQLException ignored){}
    }
    public boolean isInCart(int uid,int pid){
        try{PreparedStatement ps=conn.prepareStatement("SELECT 1 FROM cart WHERE user_id=? AND product_id=?");ps.setInt(1,uid);ps.setInt(2,pid);return ps.executeQuery().next();}catch(SQLException e){return false;}
    }
    public boolean toggleWishlist(int uid,int pid){
        if(isInWishlist(uid,pid)){try{PreparedStatement ps=conn.prepareStatement("DELETE FROM wishlist WHERE user_id=? AND product_id=?");ps.setInt(1,uid);ps.setInt(2,pid);ps.executeUpdate();}catch(SQLException ignored){}return false;}
        else{try{PreparedStatement ps=conn.prepareStatement("INSERT INTO wishlist(user_id,product_id,added_at) VALUES(?,?,?)");ps.setInt(1,uid);ps.setInt(2,pid);ps.setString(3,now());ps.executeUpdate();}catch(SQLException ignored){}return true;}
    }
    public boolean isInWishlist(int uid,int pid){
        try{PreparedStatement ps=conn.prepareStatement("SELECT 1 FROM wishlist WHERE user_id=? AND product_id=?");ps.setInt(1,uid);ps.setInt(2,pid);return ps.executeQuery().next();}catch(SQLException e){return false;}
    }
    public List<Integer> getWishlist(int uid){
        List<Integer> list=new ArrayList<>();
        try{PreparedStatement ps=conn.prepareStatement("SELECT product_id FROM wishlist WHERE user_id=? ORDER BY added_at DESC");ps.setInt(1,uid);ResultSet rs=ps.executeQuery();while(rs.next())list.add(rs.getInt("product_id"));}catch(SQLException ignored){}
        return list;
    }
    public int placeOrder(int uid,List<int[]> items,Map<Integer,Product> pmap){
        try{
            double total=items.stream().mapToDouble(i->pmap.containsKey(i[0])?pmap.get(i[0]).getPrice()*i[1]:0).sum();
            PreparedStatement ps=conn.prepareStatement("INSERT INTO orders(user_id,total_amount,status,created_at) VALUES(?,?,?,?)");
            ps.setInt(1,uid);ps.setDouble(2,total);ps.setString(3,"Placed");ps.setString(4,now());ps.executeUpdate();
            ResultSet rs=conn.createStatement().executeQuery("SELECT last_insert_rowid()");
            int oid=rs.next()?rs.getInt(1):-1;
            for(int[] item:items){Product p=pmap.get(item[0]);if(p==null)continue;
                PreparedStatement ps2=conn.prepareStatement("INSERT INTO order_items(order_id,product_id,product_name,price,quantity) VALUES(?,?,?,?,?)");
                ps2.setInt(1,oid);ps2.setInt(2,p.getProductId());ps2.setString(3,p.getName());ps2.setDouble(4,p.getPrice());ps2.setInt(5,item[1]);ps2.executeUpdate();}
            return oid;
        }catch(SQLException e){return -1;}
    }
    public List<Object[]> getOrders(int uid){
        List<Object[]> list=new ArrayList<>();
        try{PreparedStatement ps=conn.prepareStatement("SELECT o.id,o.total_amount,o.status,o.created_at,(SELECT COUNT(*) FROM order_items WHERE order_id=o.id) AS items FROM orders o WHERE o.user_id=? ORDER BY o.created_at DESC");ps.setInt(1,uid);ResultSet rs=ps.executeQuery();while(rs.next())list.add(new Object[]{rs.getInt("id"),rs.getDouble("total_amount"),rs.getString("status"),rs.getString("created_at"),rs.getInt("items")});}catch(SQLException ignored){}
        return list;
    }
    public List<Object[]> getOrderItems(int oid){
        List<Object[]> list=new ArrayList<>();
        try{PreparedStatement ps=conn.prepareStatement("SELECT product_name,price,quantity FROM order_items WHERE order_id=?");ps.setInt(1,oid);ResultSet rs=ps.executeQuery();while(rs.next()){double pr=rs.getDouble("price");int q=rs.getInt("quantity");list.add(new Object[]{rs.getString("product_name"),pr,q,pr*q});}}catch(SQLException ignored){}
        return list;
    }
}
/* ================================================================
   MAIN APPLICATION
   ================================================================ */
public class ProductCatalog extends JFrame{
    // Palette
    static final Color BG=new Color(13,17,28),SIDEBAR=new Color(17,22,36),CARD=new Color(22,28,45),CARD2=new Color(26,33,52);
    static final Color ACCENT=new Color(79,140,255),ACCENT_D=new Color(79,140,255,38),TEXT=new Color(220,228,255);
    static final Color SUBTEXT=new Color(130,145,185),MUTED=new Color(55,68,105),BORDER=new Color(35,44,70);
    static final Color GREEN=new Color(52,211,153),AMBER=new Color(251,191,36),RED_C=new Color(248,92,92);
    static final Color HDR_BG=new Color(15,20,33),ROW_A=new Color(19,25,40),ROW_B=new Color(22,29,46);
    static final Color ROW_SEL=new Color(79,140,255,55),WISH_ON=new Color(248,92,92),WISH_OFF=new Color(55,68,105);
    // Fonts
    static final Font F_TITLE=new Font("Segoe UI",Font.BOLD,22),F_H2=new Font("Segoe UI",Font.BOLD,16);
    static final Font F_H3=new Font("Segoe UI",Font.BOLD,13),F_BODY=new Font("Segoe UI",Font.PLAIN,13);
    static final Font F_BOLD=new Font("Segoe UI",Font.BOLD,13),F_SMALL=new Font("Segoe UI",Font.PLAIN,11);
    static final Font F_PRICE=new Font("Segoe UI",Font.BOLD,15);

    private final List<Product> allProducts=new ArrayList<>();
    private List<Product> view=new ArrayList<>();
    private final Map<Integer,Product> productMap=new HashMap<>();
    private int currentUserId=-1; private String currentUsername="";
    private String activeCat="All",activeSort="Default";
    private DatabaseManager db;
    private CardLayout rootLayout; private JPanel rootPanel,mainPanel;
    private JPanel catalogPanel; private JTextField searchField;
    private JLabel countLbl,statusLbl,minLbl,maxLbl,avgLbl,userGreetLbl,cartBadgeLbl,wishBadgeLbl;
    private final Map<String,JButton> catBtns=new LinkedHashMap<>();
    private CardLayout innerLayout; private JPanel innerPanel;
    private JPanel cartItemsPanel; private JLabel cartTotalLbl;
    private JPanel wishlistItemsPanel;
    private DefaultTableModel ordersTableModel;
    private JLabel acctUserLbl,acctEmailLbl,acctJoinLbl,acctCartLbl,acctWishLbl,acctOrderLbl;

    public static void main(String[] args){
        try{UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());}catch(Exception ignored){}
        UIManager.put("ScrollBar.width",8);UIManager.put("ScrollBar.thumb",new Color(50,65,100));UIManager.put("ScrollBar.track",CARD);
        SwingUtilities.invokeLater(ProductCatalog::new);
    }

    public ProductCatalog(){
        super("PriceVista -- Online Product Catalog");
        setDefaultCloseOperation(EXIT_ON_CLOSE);setSize(1250,780);setMinimumSize(new Dimension(960,620));setLocationRelativeTo(null);
        getContentPane().setBackground(BG);
        db=new DatabaseManager(); loadProducts();
        rootLayout=new CardLayout(); rootPanel=new JPanel(rootLayout); rootPanel.setBackground(BG);
        rootPanel.add(buildLoginPanel(),"login"); rootPanel.add(buildRegisterPanel(),"register");
        add(rootPanel); rootLayout.show(rootPanel,"login"); setVisible(true);
    }

    private void loadProducts(){
        Object[][] rows={
          {101,"Samsung Galaxy S24 Ultra","Electronics",89999,"6.8-inch QHD+ AMOLED, 200MP camera, Snapdragon 8 Gen 3","Samsung",4.8,50},
          {102,"Apple iPhone 15 Pro","Electronics",99999,"6.1-inch Super Retina XDR, A17 Pro chip, ProRAW camera","Apple",4.9,30},
          {103,"Sony WH-1000XM5 Headphones","Electronics",29990,"Industry-leading noise cancellation, 30hr battery","Sony",4.7,80},
          {104,"Dell XPS 15 Laptop","Electronics",149999,"15.6-inch OLED 3.5K, Intel i9, 32GB RAM, RTX 4070","Dell",4.6,20},
          {105,"LG 55 inch OLED 4K TV","Electronics",89990,"55-inch OLED evo, a9 AI Processor, Dolby Vision IQ","LG",4.7,15},
          {106,"BoAt Airdopes 141 TWS","Electronics",1299,"Up to 42 hours total playback, Beast Mode gaming","boAt",4.3,200},
          {107,"Canon EOS R50 Camera","Electronics",67990,"24.2MP APS-C sensor, 4K video, dual pixel AF","Canon",4.6,25},
          {108,"Apple iPad Air M2","Electronics",59900,"11-inch Liquid Retina, M2 chip, USB-C connector","Apple",4.8,40},
          {109,"Mi Smart Band 8","Electronics",3499,"1.62 AMOLED display, 16-day battery, 150+ sports modes","Xiaomi",4.4,150},
          {110,"Logitech MX Master 3S","Electronics",9995,"8K DPI sensor, MagSpeed scroll, quiet clicks","Logitech",4.7,60},
          {111,"Levis 501 Original Jeans","Clothing",3999,"Classic straight fit, 100% cotton denim","Levi's",4.4,100},
          {112,"Nike Air Max 270","Clothing",9995,"Max Air unit, breathable mesh upper, all-day comfort","Nike",4.5,75},
          {113,"Allen Solly Formal Shirt","Clothing",2499,"Slim fit, wrinkle-free fabric, button-down collar","Allen Solly",4.3,120},
          {114,"H&M Oversized Hoodie","Clothing",2999,"Soft fleece interior, kangaroo pocket, relaxed fit","H&M",4.2,90},
          {115,"Atomic Habits","Books",499,"Transform your life with tiny changes - James Clear","James Clear",4.9,500},
          {116,"Clean Code","Books",1299,"A handbook of agile software craftsmanship, R.C.Martin","Robert C Martin",4.8,300},
          {117,"The Pragmatic Programmer","Books",1499,"Your journey to mastery, 20th anniversary edition","Hunt & Thomas",4.7,250},
          {118,"Deep Work by Cal Newport","Books",599,"Rules for focused success in a distracted world","Cal Newport",4.6,400},
          {119,"Dyson V15 Detect Vacuum","Home & Garden",52900,"Laser dust detection, 60 min run time, HEPA filter","Dyson",4.7,18},
          {120,"Instant Pot Duo 7-in-1","Home & Garden",12999,"Pressure cooker, slow cooker, rice cooker and more","Instant Pot",4.6,45},
          {121,"Yoga Mat Pro 6mm","Sports",1999,"Non-slip surface, eco-friendly TPE, carry strap included","Boldfit",4.4,200},
          {122,"Whey Protein Gold 2 kg","Sports",3499,"24g protein per serving, chocolate flavour, lab tested","ON",4.7,80},
          {123,"Adjustable Dumbbell 20 kg","Sports",4599,"Quick-change weight system, chrome finish, anti-roll","PowerMax",4.3,35},
          {124,"Maybelline Fit Me Foundation","Beauty",599,"Natural finish, SPF 18, 40 shades available","Maybelline",4.4,300},
          {125,"Cetaphil Gentle Face Wash","Beauty",385,"Soap-free, non-comedogenic, for sensitive skin","Cetaphil",4.6,250},
        };
        for(Object[] r:rows){
            Product p=new Product((int)r[0],(String)r[1],(String)r[2],(double)(int)r[3],(String)r[4],(String)r[5],(double)r[6],(int)r[7]);
            allProducts.add(p); productMap.put(p.getProductId(),p);
        }
    }

    // ===================== LOGIN PANEL =====================
    private JPanel buildLoginPanel(){
        JPanel outer=gradBG(); outer.setLayout(new GridBagLayout());
        JPanel card=roundCard(400,460);
        GridBagConstraints gc=new GridBagConstraints(); gc.gridx=0; gc.fill=GridBagConstraints.HORIZONTAL; gc.insets=new Insets(8,0,8,0);
        JLabel title=centLbl("ShopSwing",F_TITLE,ACCENT);
        JLabel sub=centLbl("Sign in to your account",F_BODY,SUBTEXT);
        JTextField uf=authField("Enter your username"); JPasswordField pf=passFieldF("Enter your password");
        JLabel err=centLbl(" ",F_SMALL,RED_C);
        JButton loginBtn=bigBtn("Sign In",ACCENT);
        loginBtn.addActionListener(e->{
            String u=uf.getText().trim(),p=new String(pf.getPassword()).trim();
            if(u.isEmpty()||p.isEmpty()){err.setText("Please fill in all fields");return;}
            int[] res=db.loginUser(u,p);
            if(res[1]==1){currentUserId=res[0];currentUsername=u;err.setText(" ");uf.setText("");pf.setText("");launchMainApp();}
            else err.setText("Invalid username or password");
        });
        JButton regBtn=bigBtn("Create an Account",CARD2); regBtn.setForeground(ACCENT);
        regBtn.addActionListener(e->{uf.setText("");pf.setText("");err.setText(" ");rootLayout.show(rootPanel,"register");});
        gc.gridy=0;card.add(title,gc); gc.gridy=1;card.add(sub,gc);
        gc.gridy=2;gc.insets=new Insets(18,0,4,0);card.add(fldLbl("Username"),gc);
        gc.gridy=3;gc.insets=new Insets(0,0,8,0);card.add(uf,gc);
        gc.gridy=4;gc.insets=new Insets(4,0,4,0);card.add(fldLbl("Password"),gc);
        gc.gridy=5;gc.insets=new Insets(0,0,4,0);card.add(pf,gc);
        gc.gridy=6;gc.insets=new Insets(4,0,4,0);card.add(err,gc);
        gc.gridy=7;gc.insets=new Insets(6,0,6,0);card.add(loginBtn,gc);
        gc.gridy=8;gc.insets=new Insets(4,0,0,0);card.add(regBtn,gc);
        outer.add(card); return outer;
    }

    // ===================== REGISTER PANEL =====================
    private JPanel buildRegisterPanel(){
        JPanel outer=gradBG(); outer.setLayout(new GridBagLayout());
        JPanel card=roundCard(420,560);
        GridBagConstraints gc=new GridBagConstraints(); gc.gridx=0; gc.fill=GridBagConstraints.HORIZONTAL; gc.insets=new Insets(6,0,6,0);
        JTextField uf=authField("Choose a username"),emf=authField("Enter your email");
        JPasswordField p1=passFieldF("Create a password"),p2=passFieldF("Confirm password");
        JLabel err=centLbl(" ",F_SMALL,RED_C);
        JButton regBtn=bigBtn("Register",GREEN);
        regBtn.addActionListener(e->{
            String u=uf.getText().trim(),em=emf.getText().trim(),pw1=new String(p1.getPassword()).trim(),pw2=new String(p2.getPassword()).trim();
            if(u.isEmpty()||em.isEmpty()||pw1.isEmpty()||pw2.isEmpty()){err.setText("All fields required");return;}
            if(!pw1.equals(pw2)){err.setText("Passwords do not match");return;}
            if(!em.contains("@")){err.setText("Enter a valid email");return;}
            if(pw1.length()<4){err.setText("Password min 4 characters");return;}
            int id=db.registerUser(u,em,pw1);
            if(id==-1){err.setText("Username or email already in use");return;}
            currentUserId=id;currentUsername=u;uf.setText("");emf.setText("");p1.setText("");p2.setText("");err.setText(" ");launchMainApp();
        });
        JButton back=bigBtn("Back to Login",CARD2); back.setForeground(ACCENT);
        back.addActionListener(e->rootLayout.show(rootPanel,"login"));
        gc.gridy=0;card.add(centLbl("Create Account",F_TITLE,ACCENT),gc);
        gc.gridy=1;card.add(centLbl("Join ShopSwing today",F_BODY,SUBTEXT),gc);
        gc.gridy=2;gc.insets=new Insets(14,0,4,0);card.add(fldLbl("Username"),gc);
        gc.gridy=3;gc.insets=new Insets(0,0,8,0);card.add(uf,gc);
        gc.gridy=4;gc.insets=new Insets(4,0,4,0);card.add(fldLbl("Email"),gc);
        gc.gridy=5;gc.insets=new Insets(0,0,8,0);card.add(emf,gc);
        gc.gridy=6;gc.insets=new Insets(4,0,4,0);card.add(fldLbl("Password"),gc);
        gc.gridy=7;gc.insets=new Insets(0,0,8,0);card.add(p1,gc);
        gc.gridy=8;gc.insets=new Insets(4,0,4,0);card.add(fldLbl("Confirm Password"),gc);
        gc.gridy=9;gc.insets=new Insets(0,0,4,0);card.add(p2,gc);
        gc.gridy=10;gc.insets=new Insets(4,0,4,0);card.add(err,gc);
        gc.gridy=11;gc.insets=new Insets(6,0,6,0);card.add(regBtn,gc);
        gc.gridy=12;gc.insets=new Insets(4,0,0,0);card.add(back,gc);
        outer.add(card); return outer;
    }

    // ===================== LAUNCH MAIN =====================
    private void launchMainApp(){
        if(mainPanel!=null)rootPanel.remove(mainPanel);
        mainPanel=buildMainPanel(); rootPanel.add(mainPanel,"main");
        rootLayout.show(rootPanel,"main"); activeCat="All"; activeSort="Default";
        refresh(); updateBadges();
    }

    // ===================== MAIN PANEL =====================
    private JPanel buildMainPanel(){
        JPanel panel=new JPanel(new BorderLayout(0,0)); panel.setBackground(BG);
        innerLayout=new CardLayout(); innerPanel=new JPanel(innerLayout); innerPanel.setBackground(BG);
        JPanel catalogView=buildCatalogView();
        innerPanel.add(catalogView,"catalog");
        innerPanel.add(buildCartView(),"cart");
        innerPanel.add(buildWishlistView(),"wishlist");
        innerPanel.add(buildOrdersView(),"orders");
        innerPanel.add(buildAccountView(),"account");
        panel.add(buildMainTopBar(),BorderLayout.NORTH);
        panel.add(buildSidebar(),BorderLayout.WEST);
        panel.add(innerPanel,BorderLayout.CENTER);
        panel.add(buildStatusBar(),BorderLayout.SOUTH);
        return panel;
    }

    // ===================== TOP BAR =====================
    private JPanel buildMainTopBar(){
        JPanel bar=new JPanel(new BorderLayout(16,0)){
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2=(Graphics2D)g.create();
                GradientPaint gp=new GradientPaint(0,0,new Color(20,36,72),getWidth(),0,new Color(36,16,72));
                g2.setPaint(gp);g2.fillRect(0,0,getWidth(),getHeight());
                g2.setColor(BORDER);g2.fillRect(0,getHeight()-1,getWidth(),1);g2.dispose();
            }
        };
        bar.setOpaque(false); bar.setBorder(new EmptyBorder(12,22,12,22));
        JLabel logo=new JLabel("ShopSwing"); logo.setFont(F_TITLE); logo.setForeground(Color.WHITE);
        JPanel sw=srchWrap(); sw.setPreferredSize(new Dimension(300,36));
        JLabel sico=new JLabel("  Search items: "); sico.setFont(F_BODY); sico.setForeground(new Color(170,185,225));
        searchField=new JTextField(); searchField.setFont(F_BODY); searchField.setOpaque(false);
        searchField.setForeground(Color.WHITE); searchField.setCaretColor(Color.WHITE); searchField.setBorder(null);
        searchField.getDocument().addDocumentListener(new DocumentListener(){
            public void insertUpdate(DocumentEvent e){refresh();} public void removeUpdate(DocumentEvent e){refresh();} public void changedUpdate(DocumentEvent e){refresh();}
        });
        sw.add(sico,BorderLayout.WEST); sw.add(searchField,BorderLayout.CENTER);
        String[] sorts={"Default (ID)","Price Low to High","Price High to Low","Name A to Z","Name Z to A"};
        JComboBox<String> sortBox=makeCombo(sorts); sortBox.setPreferredSize(new Dimension(188,30));
        sortBox.addActionListener(e->{activeSort=(String)sortBox.getSelectedItem();refresh();});
        JPanel nav=new JPanel(new FlowLayout(FlowLayout.RIGHT,8,0)); nav.setOpaque(false);
        cartBadgeLbl=navLbl("Cart (0)"); cartBadgeLbl.setForeground(AMBER);
        wishBadgeLbl=navLbl("Wishlist (0)"); wishBadgeLbl.setForeground(RED_C);
        JLabel ordLbl=navLbl("Orders"); ordLbl.setForeground(GREEN);
        userGreetLbl=navLbl("Hi, "+currentUsername); userGreetLbl.setForeground(new Color(180,210,255));
        JLabel logLbl=navLbl("Logout"); logLbl.setForeground(new Color(180,100,100));
        cartBadgeLbl.addMouseListener(navClick(()->showSection("cart")));
        wishBadgeLbl.addMouseListener(navClick(()->showSection("wishlist")));
        ordLbl.addMouseListener(navClick(()->showSection("orders")));
        userGreetLbl.addMouseListener(navClick(()->showSection("account")));
        logLbl.addMouseListener(navClick(this::logout));
        JLabel sortLbl=new JLabel("Sort:"); sortLbl.setFont(F_BODY); sortLbl.setForeground(new Color(170,185,230));
        nav.add(sortLbl);nav.add(sortBox);nav.add(sep());nav.add(cartBadgeLbl);nav.add(sep());nav.add(wishBadgeLbl);nav.add(sep());nav.add(ordLbl);nav.add(sep());nav.add(userGreetLbl);nav.add(sep());nav.add(logLbl);
        bar.add(logo,BorderLayout.WEST); bar.add(sw,BorderLayout.CENTER); bar.add(nav,BorderLayout.EAST);
        return bar;
    }

    // ===================== SIDEBAR =====================
    private JScrollPane buildSidebar(){
        JPanel p=new JPanel(){@Override protected void paintComponent(Graphics g){g.setColor(SIDEBAR);g.fillRect(0,0,getWidth(),getHeight());}};
        p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS)); p.setBorder(new EmptyBorder(18,10,18,10));
        p.add(sbHdr("CATEGORIES")); p.add(Box.createVerticalStrut(6));
        String[] cats={"All","Electronics","Clothing","Books","Home & Garden","Sports","Beauty"};
        for(String c:cats){JButton b=catBtn(c,c);catBtns.put(c,b);p.add(b);p.add(Box.createVerticalStrut(3));}
        p.add(Box.createVerticalStrut(20)); p.add(sbHdr("SORT BY PRICE")); p.add(Box.createVerticalStrut(6));
        p.add(actBtn("Cheapest First",()->{activeSort="Price Low to High";refresh();})); p.add(Box.createVerticalStrut(3));
        p.add(actBtn("Premium First",()->{activeSort="Price High to Low";refresh();}));
        p.add(Box.createVerticalStrut(20)); p.add(sbHdr("SORT BY NAME")); p.add(Box.createVerticalStrut(6));
        p.add(actBtn("Name A to Z",()->{activeSort="Name A to Z";refresh();})); p.add(Box.createVerticalStrut(3));
        p.add(actBtn("Name Z to A",()->{activeSort="Name Z to A";refresh();}));
        p.add(Box.createVerticalStrut(20)); p.add(sbHdr("MY ACCOUNT")); p.add(Box.createVerticalStrut(6));
        p.add(actBtn("My Cart",()->showSection("cart"))); p.add(Box.createVerticalStrut(3));
        p.add(actBtn("My Wishlist",()->showSection("wishlist"))); p.add(Box.createVerticalStrut(3));
        p.add(actBtn("My Orders",()->showSection("orders"))); p.add(Box.createVerticalStrut(3));
        p.add(actBtn("My Account",()->showSection("account")));
        p.add(Box.createVerticalGlue()); p.add(Box.createVerticalStrut(16)); p.add(statsCard());
        JScrollPane sp=new JScrollPane(p); sp.setPreferredSize(new Dimension(190,0));
        sp.setBorder(BorderFactory.createMatteBorder(0,0,0,1,BORDER));
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sp.getViewport().setBackground(SIDEBAR); sp.setBackground(SIDEBAR); return sp;
    }
    private JPanel statsCard(){
        JPanel c=new JPanel(new GridLayout(3,1,0,6)){@Override protected void paintComponent(Graphics g){Graphics2D g2=(Graphics2D)g.create();g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);g2.setColor(CARD);g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),12,12));g2.setColor(BORDER);g2.draw(new RoundRectangle2D.Float(0,0,getWidth()-1,getHeight()-1,12,12));g2.dispose();}};
        c.setOpaque(false);c.setBorder(new EmptyBorder(12,14,12,14));c.setAlignmentX(LEFT_ALIGNMENT);c.setMaximumSize(new Dimension(Integer.MAX_VALUE,108));
        minLbl=sLine("Min","---",GREEN);maxLbl=sLine("Max","---",RED_C);avgLbl=sLine("Avg","---",AMBER);
        c.add(minLbl);c.add(maxLbl);c.add(avgLbl);return c;
    }

    // ===================== CATALOG VIEW =====================
    private JPanel buildCatalogView(){
        JPanel w=new JPanel(new BorderLayout(0,0));w.setBackground(BG);
        JPanel hdr=new JPanel(new BorderLayout());hdr.setBackground(CARD);hdr.setBorder(new EmptyBorder(10,18,10,18));
        JLabel t=new JLabel("Product Catalog");t.setFont(F_H2);t.setForeground(TEXT);
        countLbl=new JLabel("0 items");countLbl.setFont(F_BOLD);countLbl.setForeground(ACCENT);
        hdr.add(t,BorderLayout.WEST);hdr.add(countLbl,BorderLayout.EAST);
        catalogPanel=new JPanel(new GridLayout(0,3,14,14));catalogPanel.setBackground(BG);catalogPanel.setBorder(new EmptyBorder(14,14,14,14));
        JScrollPane sc=new JScrollPane(catalogPanel);sc.setBorder(null);sc.getViewport().setBackground(BG);sc.setBackground(BG);sc.getVerticalScrollBar().setUnitIncrement(20);
        JPanel ft=new JPanel(new FlowLayout(FlowLayout.LEFT,16,7));ft.setBackground(HDR_BG);ft.setBorder(BorderFactory.createMatteBorder(1,0,0,0,BORDER));
        statusLbl=new JLabel("Ready");statusLbl.setFont(F_SMALL);statusLbl.setForeground(MUTED);ft.add(statusLbl);
        w.add(hdr,BorderLayout.NORTH);w.add(sc,BorderLayout.CENTER);w.add(ft,BorderLayout.SOUTH);return w;
    }

    // ===================== CART VIEW =====================
    private JPanel buildCartView(){
        JPanel p=new JPanel(new BorderLayout(0,14));p.setBackground(BG);p.setBorder(new EmptyBorder(14,14,14,14));
        p.add(secHdr("My Cart",()->showSection("catalog")),BorderLayout.NORTH);
        cartItemsPanel=new JPanel();cartItemsPanel.setLayout(new BoxLayout(cartItemsPanel,BoxLayout.Y_AXIS));cartItemsPanel.setBackground(BG);
        JScrollPane sc=new JScrollPane(cartItemsPanel);sc.setBorder(null);sc.getViewport().setBackground(BG);sc.setBackground(BG);sc.getVerticalScrollBar().setUnitIncrement(16);
        JPanel sum=new JPanel(new BorderLayout(0,14)){@Override protected void paintComponent(Graphics g){Graphics2D g2=(Graphics2D)g.create();g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);g2.setColor(CARD);g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),14,14));g2.dispose();}};
        sum.setOpaque(false);sum.setPreferredSize(new Dimension(260,0));sum.setBorder(new EmptyBorder(20,18,20,18));
        JLabel st=new JLabel("Order Summary");st.setFont(F_H2);st.setForeground(TEXT);
        cartTotalLbl=new JLabel("Total: Rs 0");cartTotalLbl.setFont(F_PRICE);cartTotalLbl.setForeground(AMBER);
        JButton chk=bigBtn("Place Order",GREEN);chk.addActionListener(e->placeOrder());
        sum.add(st,BorderLayout.NORTH);sum.add(cartTotalLbl,BorderLayout.CENTER);sum.add(chk,BorderLayout.SOUTH);
        p.add(sc,BorderLayout.CENTER);p.add(sum,BorderLayout.EAST);return p;
    }

    // ===================== WISHLIST VIEW =====================
    private JPanel buildWishlistView(){
        JPanel p=new JPanel(new BorderLayout(0,14));p.setBackground(BG);p.setBorder(new EmptyBorder(14,14,14,14));
        p.add(secHdr("My Wishlist",()->showSection("catalog")),BorderLayout.NORTH);
        wishlistItemsPanel=new JPanel(new GridLayout(0,3,14,14));wishlistItemsPanel.setBackground(BG);
        JScrollPane sc=new JScrollPane(wishlistItemsPanel);sc.setBorder(null);sc.getViewport().setBackground(BG);sc.setBackground(BG);sc.getVerticalScrollBar().setUnitIncrement(16);
        p.add(sc,BorderLayout.CENTER);return p;
    }

    // ===================== ORDERS VIEW =====================
    private JPanel buildOrdersView(){
        JPanel p=new JPanel(new BorderLayout(0,14));p.setBackground(BG);p.setBorder(new EmptyBorder(14,14,14,14));
        p.add(secHdr("My Orders",()->showSection("catalog")),BorderLayout.NORTH);
        String[] cols={"Order ID","Date & Time","Items","Total (Rs)","Status"};
        ordersTableModel=new DefaultTableModel(cols,0){@Override public boolean isCellEditable(int r,int c){return false;}};
        JTable ot=new JTable(ordersTableModel){@Override public Component prepareRenderer(TableCellRenderer r,int row,int col){Component c=super.prepareRenderer(r,row,col);boolean sel=isRowSelected(row);c.setBackground(sel?ROW_SEL:(row%2==0?ROW_A:ROW_B));c.setForeground(sel?Color.WHITE:TEXT);if(c instanceof JComponent)((JComponent)c).setBorder(new EmptyBorder(8,11,8,11));return c;}};
        styleOrdTbl(ot);
        ot.addMouseListener(new MouseAdapter(){@Override public void mouseClicked(MouseEvent e){if(e.getClickCount()==2&&ot.getSelectedRow()>=0){int oid=(int)ordersTableModel.getValueAt(ot.getSelectedRow(),0);showOrderDetail(oid);}}});
        JScrollPane sc=new JScrollPane(ot);sc.setBorder(BorderFactory.createLineBorder(BORDER));sc.getViewport().setBackground(ROW_A);sc.setBackground(BG);
        JPanel ft=new JPanel(new FlowLayout(FlowLayout.LEFT,14,6));ft.setBackground(HDR_BG);ft.setBorder(BorderFactory.createMatteBorder(1,0,0,0,BORDER));
        JLabel h=new JLabel("Tip: Double-click an order to view its items");h.setFont(F_SMALL);h.setForeground(MUTED);ft.add(h);
        p.add(sc,BorderLayout.CENTER);p.add(ft,BorderLayout.SOUTH);return p;
    }

    // ===================== ACCOUNT VIEW =====================
    private JPanel buildAccountView(){
        JPanel p=new JPanel(new BorderLayout(0,14));p.setBackground(BG);p.setBorder(new EmptyBorder(14,14,14,14));
        p.add(secHdr("My Account",()->showSection("catalog")),BorderLayout.NORTH);
        JPanel center=new JPanel(new GridBagLayout());center.setBackground(BG);
        JPanel card=roundCard(440,440);
        GridBagConstraints gc=new GridBagConstraints();gc.gridx=0;gc.fill=GridBagConstraints.HORIZONTAL;gc.insets=new Insets(10,0,10,0);
        JLabel av=new JLabel("[ USER ]",SwingConstants.CENTER);av.setFont(new Font("Segoe UI",Font.BOLD,30));av.setForeground(ACCENT);av.setBorder(new EmptyBorder(0,0,10,0));
        acctUserLbl=iRow("Username","---");acctEmailLbl=iRow("Email","---");acctJoinLbl=iRow("Joined","---");
        acctCartLbl=iRow("Cart Items","---");acctWishLbl=iRow("Wishlist","---");acctOrderLbl=iRow("Orders","---");
        JButton lo=bigBtn("Sign Out",new Color(100,30,30));lo.addActionListener(e->logout());
        gc.gridy=0;card.add(av,gc);gc.gridy=1;card.add(acctUserLbl,gc);gc.gridy=2;card.add(acctEmailLbl,gc);
        gc.gridy=3;card.add(acctJoinLbl,gc);gc.gridy=4;card.add(acctCartLbl,gc);gc.gridy=5;card.add(acctWishLbl,gc);
        gc.gridy=6;card.add(acctOrderLbl,gc);gc.gridy=7;gc.insets=new Insets(16,0,0,0);card.add(lo,gc);
        center.add(card);p.add(center,BorderLayout.CENTER);return p;
    }

    // ===================== STATUS BAR =====================
    private JPanel buildStatusBar(){
        JPanel b=new JPanel(new BorderLayout());b.setBackground(new Color(10,13,22));b.setBorder(new EmptyBorder(5,18,5,18));
        statusLbl=new JLabel("Ready");statusLbl.setFont(F_SMALL);statusLbl.setForeground(SUBTEXT);
        JLabel c=new JLabel("ShopSwing  |  Advanced Java  |  Swing  |  SQLite");c.setFont(F_SMALL);c.setForeground(MUTED);
        b.add(statusLbl,BorderLayout.WEST);b.add(c,BorderLayout.EAST);return b;
    }

    // ===================== SECTION SWITCH =====================
    private void showSection(String key){
        if(key.equals("cart"))refreshCartView();if(key.equals("wishlist"))refreshWishlistView();
        if(key.equals("orders"))refreshOrdersView();if(key.equals("account"))refreshAccountView();
        if(key.equals("catalog"))refresh();
        innerLayout.show(innerPanel,key);
    }

    // ===================== REFRESH CATALOG =====================
    private void refresh(){
        String q=searchField==null?"":searchField.getText().trim().toLowerCase();
        view=allProducts.stream()
            .filter(p->activeCat.equals("All")||p.getCategory().equals(activeCat))
            .filter(p->q.isEmpty()||p.getName().toLowerCase().contains(q)||p.getCategory().toLowerCase().contains(q)||String.valueOf(p.getProductId()).contains(q))
            .collect(Collectors.toList());
        if(activeSort.contains("Low to High"))view.sort(new PriceComparator(true));
        else if(activeSort.contains("High to Low"))view.sort(new PriceComparator(false));
        else if(activeSort.contains("A to Z"))view.sort(new NameComparator(true));
        else if(activeSort.contains("Z to A"))view.sort(new NameComparator(false));
        else view.sort(Comparator.comparingInt(Product::getProductId));
        if(catalogPanel==null)return;
        catalogPanel.removeAll();
        if(view.isEmpty()){catalogPanel.setLayout(new GridBagLayout());JLabel e=new JLabel("No products found",SwingConstants.CENTER);e.setFont(F_H3);e.setForeground(MUTED);catalogPanel.add(e);}
        else{catalogPanel.setLayout(new GridLayout(0,3,14,14));for(Product p:view)catalogPanel.add(prodCard(p));}
        if(!view.isEmpty()){double min=view.stream().mapToDouble(Product::getPrice).min().orElse(0),max=view.stream().mapToDouble(Product::getPrice).max().orElse(0),avg=view.stream().mapToDouble(Product::getPrice).average().orElse(0);
            if(minLbl!=null)minLbl.setText("Min  Rs "+String.format("%,.0f",min));if(maxLbl!=null)maxLbl.setText("Max  Rs "+String.format("%,.0f",max));if(avgLbl!=null)avgLbl.setText("Avg  Rs "+String.format("%,.0f",avg));}
        if(countLbl!=null)countLbl.setText(view.size()+" item"+(view.size()!=1?"s":""));
        if(statusLbl!=null)statusLbl.setText("Showing "+view.size()+" / "+allProducts.size()+" products  |  "+(activeCat.equals("All")?"All categories":activeCat));
        refreshCatBtns();catalogPanel.revalidate();catalogPanel.repaint();
    }

    // ===================== PRODUCT CARD =====================
    private JPanel prodCard(Product p){
        JPanel card=new JPanel(new BorderLayout(0,0)){
            @Override protected void paintComponent(Graphics g){Graphics2D g2=(Graphics2D)g.create();g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);g2.setColor(getBackground());g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),14,14));g2.setColor(BORDER);g2.draw(new RoundRectangle2D.Float(0,0,getWidth()-1,getHeight()-1,14,14));g2.dispose();}
        };
        card.setBackground(CARD);card.setOpaque(false);card.setPreferredSize(new Dimension(0,250));card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new MouseAdapter(){
            @Override public void mouseEntered(MouseEvent e){card.setBackground(CARD2);card.repaint();}
            @Override public void mouseExited(MouseEvent e){card.setBackground(CARD);card.repaint();}
            @Override public void mouseClicked(MouseEvent e){showProductDetail(p);}
        });
        JPanel top=new JPanel(new BorderLayout());top.setOpaque(false);top.setBorder(new EmptyBorder(10,12,4,10));
        JLabel cp=pillLbl(p.getCategory());
        boolean w=db.isInWishlist(currentUserId,p.getProductId());
        JButton wb=new JButton(w?"Saved":"Save"){@Override protected void paintComponent(Graphics g){Graphics2D g2=(Graphics2D)g.create();g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);boolean ww=db.isInWishlist(currentUserId,p.getProductId());g2.setColor(ww?new Color(248,92,92,50):new Color(55,68,105,60));g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),8,8));super.paintComponent(g);g2.dispose();}};
        wb.setFont(F_SMALL);wb.setForeground(w?WISH_ON:WISH_OFF);wb.setBorder(new EmptyBorder(3,8,3,8));wb.setOpaque(false);wb.setContentAreaFilled(false);wb.setBorderPainted(false);wb.setFocusPainted(false);wb.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        wb.addActionListener(e->{boolean added=db.toggleWishlist(currentUserId,p.getProductId());wb.setText(added?"Saved":"Save");wb.setForeground(added?WISH_ON:WISH_OFF);wb.repaint();updateBadges();});
        top.add(cp,BorderLayout.WEST);top.add(wb,BorderLayout.EAST);
        JPanel info=new JPanel();info.setLayout(new BoxLayout(info,BoxLayout.Y_AXIS));info.setOpaque(false);info.setBorder(new EmptyBorder(4,14,8,14));
        JLabel nl=new JLabel("<html><b>"+henc(p.getName())+"</b></html>");nl.setFont(F_BOLD);nl.setForeground(TEXT);nl.setAlignmentX(LEFT_ALIGNMENT);
        JLabel bl=new JLabel(p.getBrand());bl.setFont(F_SMALL);bl.setForeground(MUTED);bl.setAlignmentX(LEFT_ALIGNMENT);
        JPanel rp=new JPanel(new FlowLayout(FlowLayout.LEFT,2,0));rp.setOpaque(false);
        int sr=(int)Math.round(p.getRating());
        for(int i=1;i<=5;i++){JLabel s=new JLabel(i<=sr?"*":"-");s.setFont(new Font("Segoe UI",Font.BOLD,12));s.setForeground(i<=sr?AMBER:MUTED);rp.add(s);}
        JLabel rv=new JLabel(" "+p.getRating());rv.setFont(F_SMALL);rv.setForeground(SUBTEXT);rp.add(rv);
        JLabel pl=new JLabel("Rs "+String.format("%,.0f",p.getPrice()));pl.setFont(F_PRICE);pl.setForeground(AMBER);pl.setAlignmentX(LEFT_ALIGNMENT);
        info.add(nl);info.add(Box.createVerticalStrut(2));info.add(bl);info.add(Box.createVerticalStrut(4));info.add(rp);info.add(Box.createVerticalStrut(6));info.add(pl);
        JPanel br=new JPanel(new GridLayout(1,2,6,0));br.setOpaque(false);br.setBorder(new EmptyBorder(0,12,12,12));
        JButton cb=smBtn("Add to Cart",ACCENT); cb.addActionListener(e->{db.addToCart(currentUserId,p.getProductId());updateBadges();JOptionPane.showMessageDialog(this,p.getName()+" added to cart!","Cart",JOptionPane.INFORMATION_MESSAGE);});
        JButton db2=smBtn("View Details",CARD2);db2.setForeground(ACCENT);db2.addActionListener(e->showProductDetail(p));
        br.add(cb);br.add(db2);
        card.add(top,BorderLayout.NORTH);card.add(info,BorderLayout.CENTER);card.add(br,BorderLayout.SOUTH);
        return card;
    }

    // ===================== PRODUCT DETAIL =====================
    private void showProductDetail(Product p){
        JPanel dp=new JPanel(new BorderLayout(0,16));dp.setBackground(BG);dp.setBorder(new EmptyBorder(16,16,16,16));
        dp.add(secHdr("Product Details",()->showSection("catalog")),BorderLayout.NORTH);
        JPanel content=new JPanel(new BorderLayout(20,0));content.setBackground(BG);
        // Left image area
        JPanel img=new JPanel(new GridBagLayout()){@Override protected void paintComponent(Graphics g){Graphics2D g2=(Graphics2D)g.create();g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);Color c=catCol(p.getCategory());g2.setColor(new Color(c.getRed(),c.getGreen(),c.getBlue(),40));g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),20,20));g2.setColor(new Color(c.getRed(),c.getGreen(),c.getBlue(),80));g2.draw(new RoundRectangle2D.Float(0,0,getWidth()-1,getHeight()-1,20,20));g2.dispose();}};
        img.setPreferredSize(new Dimension(280,0));img.setOpaque(false);
        JLabel ci=new JLabel(catInit(p.getCategory()),SwingConstants.CENTER);Color cc=catCol(p.getCategory());
        ci.setFont(new Font("Segoe UI",Font.BOLD,60));ci.setForeground(new Color(cc.getRed(),cc.getGreen(),cc.getBlue(),180));img.add(ci);
        // Right detail card
        JPanel right=new JPanel(){@Override protected void paintComponent(Graphics g){Graphics2D g2=(Graphics2D)g.create();g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);g2.setColor(CARD);g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),16,16));g2.setColor(BORDER);g2.draw(new RoundRectangle2D.Float(0,0,getWidth()-1,getHeight()-1,16,16));g2.dispose();}};
        right.setLayout(new BoxLayout(right,BoxLayout.Y_AXIS));right.setOpaque(false);right.setBorder(new EmptyBorder(24,28,24,28));
        JLabel nl=new JLabel(p.getName());nl.setFont(new Font("Segoe UI",Font.BOLD,20));nl.setForeground(TEXT);nl.setAlignmentX(LEFT_ALIGNMENT);
        JPanel br=new JPanel(new FlowLayout(FlowLayout.LEFT,10,0));br.setOpaque(false);br.setAlignmentX(LEFT_ALIGNMENT);br.add(pillLbl(p.getCategory()));
        JLabel brl=new JLabel("by "+p.getBrand());brl.setFont(F_BODY);brl.setForeground(SUBTEXT);br.add(brl);
        JPanel stars=new JPanel(new FlowLayout(FlowLayout.LEFT,2,0));stars.setOpaque(false);stars.setAlignmentX(LEFT_ALIGNMENT);
        int sr=(int)Math.round(p.getRating());for(int i=1;i<=5;i++){JLabel s=new JLabel(i<=sr?"*":"-");s.setFont(new Font("Segoe UI",Font.BOLD,16));s.setForeground(i<=sr?AMBER:MUTED);stars.add(s);}
        JLabel rv=new JLabel("  "+p.getRating()+" / 5.0");rv.setFont(F_BODY);rv.setForeground(SUBTEXT);stars.add(rv);
        JLabel pr=new JLabel("Rs "+String.format("%,.2f",p.getPrice()));pr.setFont(new Font("Segoe UI",Font.BOLD,26));pr.setForeground(AMBER);pr.setAlignmentX(LEFT_ALIGNMENT);
        JSeparator sep=new JSeparator();sep.setForeground(BORDER);sep.setAlignmentX(LEFT_ALIGNMENT);sep.setMaximumSize(new Dimension(Integer.MAX_VALUE,1));
        JLabel dt=new JLabel("Description");dt.setFont(F_H3);dt.setForeground(SUBTEXT);dt.setAlignmentX(LEFT_ALIGNMENT);
        JTextArea da=new JTextArea(p.getDescription());da.setFont(F_BODY);da.setForeground(TEXT);da.setBackground(CARD);da.setLineWrap(true);da.setWrapStyleWord(true);da.setEditable(false);da.setBorder(null);da.setAlignmentX(LEFT_ALIGNMENT);da.setMaximumSize(new Dimension(Integer.MAX_VALUE,60));
        JPanel dg=new JPanel(new GridLayout(3,2,10,10));dg.setOpaque(false);dg.setAlignmentX(LEFT_ALIGNMENT);dg.setMaximumSize(new Dimension(Integer.MAX_VALUE,90));dg.setBorder(new EmptyBorder(8,0,8,0));
        dg.add(dCell("Product ID","#"+p.getProductId()));dg.add(dCell("Category",p.getCategory()));dg.add(dCell("Brand",p.getBrand()));dg.add(dCell("In Stock",p.getStock()+" units"));dg.add(dCell("Delivery","Free Shipping"));dg.add(dCell("Returns","30-Day Policy"));
        JPanel ar=new JPanel(new FlowLayout(FlowLayout.LEFT,12,0));ar.setOpaque(false);ar.setAlignmentX(LEFT_ALIGNMENT);
        JButton acb=bigBtn("Add to Cart",ACCENT);acb.addActionListener(e->{db.addToCart(currentUserId,p.getProductId());updateBadges();JOptionPane.showMessageDialog(this,p.getName()+" added to cart!","Cart",JOptionPane.INFORMATION_MESSAGE);});
        boolean iw=db.isInWishlist(currentUserId,p.getProductId());
        JButton wb=bigBtn(iw?"Remove from Wishlist":"Add to Wishlist",iw?WISH_ON:CARD2);wb.setForeground(iw?Color.WHITE:ACCENT);
        wb.addActionListener(e->{boolean added=db.toggleWishlist(currentUserId,p.getProductId());wb.setText(added?"Remove from Wishlist":"Add to Wishlist");wb.repaint();updateBadges();});
        ar.add(acb);ar.add(wb);
        right.add(nl);right.add(Box.createVerticalStrut(8));right.add(br);right.add(Box.createVerticalStrut(8));right.add(stars);right.add(Box.createVerticalStrut(12));right.add(pr);right.add(Box.createVerticalStrut(14));right.add(sep);right.add(Box.createVerticalStrut(12));right.add(dt);right.add(Box.createVerticalStrut(4));right.add(da);right.add(Box.createVerticalStrut(12));right.add(dg);right.add(Box.createVerticalStrut(14));right.add(ar);right.add(Box.createVerticalGlue());
        content.add(img,BorderLayout.WEST);content.add(right,BorderLayout.CENTER);
        dp.add(content,BorderLayout.CENTER);
        innerPanel.add(dp,"detail");innerLayout.show(innerPanel,"detail");
    }

    // ===================== REFRESH CART =====================
    private void refreshCartView(){
        cartItemsPanel.removeAll();
        List<int[]> items=db.getCart(currentUserId); double total=0;
        if(items.isEmpty()){JLabel e=new JLabel("Your cart is empty. Start shopping!",SwingConstants.CENTER);e.setFont(F_H3);e.setForeground(MUTED);cartItemsPanel.add(e);}
        for(int[] item:items){Product p=productMap.get(item[0]);if(p==null)continue;total+=p.getPrice()*item[1];cartItemsPanel.add(cartRow(p,item[1]));cartItemsPanel.add(Box.createVerticalStrut(8));}
        if(cartTotalLbl!=null)cartTotalLbl.setText("Total: Rs "+String.format("%,.2f",total));
        cartItemsPanel.revalidate();cartItemsPanel.repaint();
    }
    private JPanel cartRow(Product p,int qty){
        JPanel row=new JPanel(new BorderLayout(12,0)){@Override protected void paintComponent(Graphics g){Graphics2D g2=(Graphics2D)g.create();g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);g2.setColor(CARD);g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),12,12));g2.setColor(BORDER);g2.draw(new RoundRectangle2D.Float(0,0,getWidth()-1,getHeight()-1,12,12));g2.dispose();}};
        row.setOpaque(false);row.setBorder(new EmptyBorder(14,18,14,18));row.setMaximumSize(new Dimension(Integer.MAX_VALUE,80));row.setAlignmentX(LEFT_ALIGNMENT);
        JLabel ic=new JLabel(catInit(p.getCategory()),SwingConstants.CENTER);ic.setFont(new Font("Segoe UI",Font.BOLD,18));ic.setForeground(catCol(p.getCategory()));ic.setPreferredSize(new Dimension(50,50));
        JPanel mid=new JPanel(new GridLayout(2,1,0,2));mid.setOpaque(false);
        JLabel nl=new JLabel(p.getName());nl.setFont(F_BOLD);nl.setForeground(TEXT);
        JLabel pl=new JLabel("Rs "+String.format("%,.0f",p.getPrice())+" each");pl.setFont(F_SMALL);pl.setForeground(AMBER);
        mid.add(nl);mid.add(pl);
        JPanel right=new JPanel(new FlowLayout(FlowLayout.RIGHT,8,0));right.setOpaque(false);
        JLabel sub=new JLabel("Rs "+String.format("%,.0f",p.getPrice()*qty));sub.setFont(F_PRICE);sub.setForeground(AMBER);
        JButton mn=tBtn("-"),pl2=tBtn("+"),dl=tBtn("X");dl.setForeground(RED_C);
        JLabel ql=new JLabel("  "+qty+"  ");ql.setFont(F_BOLD);ql.setForeground(TEXT);
        mn.addActionListener(e->{db.updateCartQty(currentUserId,p.getProductId(),qty-1);refreshCartView();updateBadges();});
        pl2.addActionListener(e->{db.updateCartQty(currentUserId,p.getProductId(),qty+1);refreshCartView();updateBadges();});
        dl.addActionListener(e->{db.removeFromCart(currentUserId,p.getProductId());refreshCartView();updateBadges();});
        right.add(mn);right.add(ql);right.add(pl2);right.add(Box.createHorizontalStrut(8));right.add(sub);right.add(Box.createHorizontalStrut(8));right.add(dl);
        row.add(ic,BorderLayout.WEST);row.add(mid,BorderLayout.CENTER);row.add(right,BorderLayout.EAST);return row;
    }
    private void placeOrder(){
        List<int[]> items=db.getCart(currentUserId);
        if(items.isEmpty()){JOptionPane.showMessageDialog(this,"Your cart is empty!","Order",JOptionPane.WARNING_MESSAGE);return;}
        int oid=db.placeOrder(currentUserId,items,productMap);
        if(oid>0){db.clearCart(currentUserId);updateBadges();refreshCartView();JOptionPane.showMessageDialog(this,"Order #"+oid+" placed successfully!\nThank you for shopping with ShopSwing.","Order Confirmed",JOptionPane.INFORMATION_MESSAGE);showSection("orders");}
    }
    private void refreshWishlistView(){
        wishlistItemsPanel.removeAll();List<Integer> ids=db.getWishlist(currentUserId);
        if(ids.isEmpty()){wishlistItemsPanel.setLayout(new GridBagLayout());JLabel e=new JLabel("Your wishlist is empty.",SwingConstants.CENTER);e.setFont(F_H3);e.setForeground(MUTED);wishlistItemsPanel.add(e);}
        else{wishlistItemsPanel.setLayout(new GridLayout(0,3,14,14));for(int pid:ids){Product p=productMap.get(pid);if(p!=null)wishlistItemsPanel.add(prodCard(p));}}
        wishlistItemsPanel.revalidate();wishlistItemsPanel.repaint();
    }
    private void refreshOrdersView(){
        ordersTableModel.setRowCount(0);
        List<Object[]> orders=db.getOrders(currentUserId);
        for(Object[] o:orders)ordersTableModel.addRow(new Object[]{o[0],o[3],o[4],String.format("Rs %,.2f",(Double)o[1]),o[2]});
    }
    private void showOrderDetail(int oid){
        JDialog dlg=new JDialog(this,"Order #"+oid+" Details",true);dlg.setSize(560,420);dlg.setLocationRelativeTo(this);dlg.getContentPane().setBackground(BG);dlg.setLayout(new BorderLayout(0,0));
        JPanel hdr=new JPanel(new BorderLayout()){@Override protected void paintComponent(Graphics g){Graphics2D g2=(Graphics2D)g.create();GradientPaint gp=new GradientPaint(0,0,new Color(20,38,75),getWidth(),0,new Color(40,16,75));g2.setPaint(gp);g2.fillRect(0,0,getWidth(),getHeight());g2.dispose();}};
        hdr.setBorder(new EmptyBorder(16,22,16,22));JLabel t=new JLabel("Order #"+oid);t.setFont(F_H2);t.setForeground(Color.WHITE);hdr.add(t,BorderLayout.WEST);dlg.add(hdr,BorderLayout.NORTH);
        String[] cols={"Product","Price","Qty","Subtotal (Rs)"};DefaultTableModel dtm=new DefaultTableModel(cols,0){@Override public boolean isCellEditable(int r,int c){return false;}};
        double gt=0;for(Object[] item:db.getOrderItems(oid)){dtm.addRow(new Object[]{item[0],String.format("Rs %,.0f",(Double)item[1]),item[2],String.format("Rs %,.0f",(Double)item[3])});gt+=(Double)item[3];}
        JTable t2=new JTable(dtm);t2.setFont(F_BODY);t2.setRowHeight(32);t2.setBackground(ROW_A);t2.setForeground(TEXT);t2.setShowGrid(false);t2.setFocusable(false);t2.getTableHeader().setBackground(HDR_BG);t2.getTableHeader().setForeground(SUBTEXT);t2.getTableHeader().setFont(F_H3);
        JScrollPane sc=new JScrollPane(t2);sc.setBorder(null);sc.getViewport().setBackground(ROW_A);dlg.add(sc,BorderLayout.CENTER);
        JPanel ft=new JPanel(new BorderLayout());ft.setBackground(HDR_BG);ft.setBorder(new EmptyBorder(10,22,10,22));
        JLabel tot=new JLabel("Grand Total:  Rs "+String.format("%,.2f",gt));tot.setFont(F_PRICE);tot.setForeground(AMBER);
        JButton cl=glwBtn("Close",MUTED);cl.addActionListener(e->dlg.dispose());ft.add(tot,BorderLayout.WEST);ft.add(cl,BorderLayout.EAST);dlg.add(ft,BorderLayout.SOUTH);dlg.setVisible(true);
    }
    private void refreshAccountView(){
        String[] info=db.getUserInfo(currentUserId);int cc=db.getCart(currentUserId).size(),wc=db.getWishlist(currentUserId).size(),oc=db.getOrders(currentUserId).size();
        if(acctUserLbl!=null)acctUserLbl.setText("Username:  "+info[0]);if(acctEmailLbl!=null)acctEmailLbl.setText("Email:  "+info[1]);if(acctJoinLbl!=null)acctJoinLbl.setText("Joined:  "+info[2]);
        if(acctCartLbl!=null)acctCartLbl.setText("Cart Items:  "+cc);if(acctWishLbl!=null)acctWishLbl.setText("Wishlist Items:  "+wc);if(acctOrderLbl!=null)acctOrderLbl.setText("Total Orders:  "+oc);
    }
    private void updateBadges(){
        if(cartBadgeLbl!=null)cartBadgeLbl.setText("Cart ("+db.getCart(currentUserId).size()+")");
        if(wishBadgeLbl!=null)wishBadgeLbl.setText("Wishlist ("+db.getWishlist(currentUserId).size()+")");
        if(userGreetLbl!=null)userGreetLbl.setText("Hi, "+currentUsername);
    }
    private void logout(){currentUserId=-1;currentUsername="";activeCat="All";activeSort="Default";rootLayout.show(rootPanel,"login");}
    private void refreshCatBtns(){catBtns.forEach((c,b)->{b.setForeground(c.equals(activeCat)?ACCENT:SUBTEXT);b.repaint();});}

    // ===================== WIDGET HELPERS =====================
    private JPanel gradBG(){return new JPanel(){@Override protected void paintComponent(Graphics g){Graphics2D g2=(Graphics2D)g.create();GradientPaint gp=new GradientPaint(0,0,new Color(10,14,24),getWidth(),getHeight(),new Color(20,15,40));g2.setPaint(gp);g2.fillRect(0,0,getWidth(),getHeight());g2.dispose();}};}
    private JPanel roundCard(int w,int h){JPanel c=new JPanel(new GridBagLayout()){@Override protected void paintComponent(Graphics g){Graphics2D g2=(Graphics2D)g.create();g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);g2.setColor(CARD);g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),20,20));g2.setColor(BORDER);g2.draw(new RoundRectangle2D.Float(0,0,getWidth()-1,getHeight()-1,20,20));g2.dispose();}};c.setOpaque(false);c.setPreferredSize(new Dimension(w,h));c.setBorder(new EmptyBorder(36,40,36,40));return c;}
    private JLabel centLbl(String t,Font f,Color c){JLabel l=new JLabel(t,SwingConstants.CENTER);l.setFont(f);l.setForeground(c);return l;}
    private JLabel fldLbl(String t){JLabel l=new JLabel(t);l.setFont(F_H3);l.setForeground(SUBTEXT);l.setAlignmentX(LEFT_ALIGNMENT);return l;}
    private JTextField authField(String h){JTextField tf=new JTextField();tf.setFont(F_BODY);tf.setBackground(CARD2);tf.setForeground(TEXT);tf.setCaretColor(TEXT);tf.setBorder(new CompoundBorder(BorderFactory.createLineBorder(BORDER),new EmptyBorder(9,12,9,12)));tf.setToolTipText(h);return tf;}
    private JPasswordField passFieldF(String h){JPasswordField tf=new JPasswordField();tf.setFont(F_BODY);tf.setBackground(CARD2);tf.setForeground(TEXT);tf.setCaretColor(TEXT);tf.setBorder(new CompoundBorder(BorderFactory.createLineBorder(BORDER),new EmptyBorder(9,12,9,12)));tf.setToolTipText(h);return tf;}
    private JButton bigBtn(String t,Color c){JButton b=new JButton(t){@Override protected void paintComponent(Graphics g){Graphics2D g2=(Graphics2D)g.create();g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);g2.setColor(getModel().isRollover()?c.brighter():c);g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),10,10));super.paintComponent(g);g2.dispose();}};b.setFont(F_BOLD);b.setForeground(Color.WHITE);b.setMaximumSize(new Dimension(Integer.MAX_VALUE,40));b.setAlignmentX(LEFT_ALIGNMENT);b.setBorder(new EmptyBorder(9,22,9,22));b.setOpaque(false);b.setContentAreaFilled(false);b.setBorderPainted(false);b.setFocusPainted(false);b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));return b;}
    private JButton smBtn(String t,Color c){JButton b=new JButton(t){@Override protected void paintComponent(Graphics g){Graphics2D g2=(Graphics2D)g.create();g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);g2.setColor(getModel().isRollover()?c.brighter():c);g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),8,8));super.paintComponent(g);g2.dispose();}};b.setFont(F_SMALL);b.setForeground(Color.WHITE);b.setBorder(new EmptyBorder(6,12,6,12));b.setOpaque(false);b.setContentAreaFilled(false);b.setBorderPainted(false);b.setFocusPainted(false);b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));return b;}
    private JButton tBtn(String t){JButton b=new JButton(t){@Override protected void paintComponent(Graphics g){Graphics2D g2=(Graphics2D)g.create();g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);g2.setColor(getModel().isRollover()?CARD2:CARD);g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),6,6));g2.setColor(BORDER);g2.draw(new RoundRectangle2D.Float(0,0,getWidth()-1,getHeight()-1,6,6));super.paintComponent(g);g2.dispose();}};b.setFont(F_BOLD);b.setForeground(TEXT);b.setPreferredSize(new Dimension(28,28));b.setBorder(new EmptyBorder(2,4,2,4));b.setOpaque(false);b.setContentAreaFilled(false);b.setBorderPainted(false);b.setFocusPainted(false);b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));return b;}
    private JButton glwBtn(String t,Color c){JButton b=new JButton(t){@Override protected void paintComponent(Graphics g){Graphics2D g2=(Graphics2D)g.create();g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);g2.setColor(getModel().isRollover()?c.brighter():c);g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),10,10));super.paintComponent(g);g2.dispose();}};b.setFont(F_BOLD);b.setForeground(Color.WHITE);b.setBorder(new EmptyBorder(8,20,8,20));b.setOpaque(false);b.setContentAreaFilled(false);b.setBorderPainted(false);b.setFocusPainted(false);b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));return b;}
    private JButton catBtn(String lbl,String cat){JButton b=new JButton(lbl){@Override protected void paintComponent(Graphics g){boolean ac=cat.equals(activeCat);Graphics2D g2=(Graphics2D)g.create();g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);if(ac){g2.setColor(ACCENT_D);g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),8,8));g2.setColor(ACCENT);g2.fillRoundRect(0,4,3,getHeight()-8,3,3);}else if(getModel().isRollover()){g2.setColor(new Color(38,48,80));g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),8,8));}super.paintComponent(g);g2.dispose();}};b.setFont(F_BODY);b.setForeground(cat.equals(activeCat)?ACCENT:SUBTEXT);b.setHorizontalAlignment(SwingConstants.LEFT);b.setBorder(new EmptyBorder(9,14,9,8));b.setMaximumSize(new Dimension(Integer.MAX_VALUE,37));b.setAlignmentX(LEFT_ALIGNMENT);b.setOpaque(false);b.setContentAreaFilled(false);b.setBorderPainted(false);b.setFocusPainted(false);b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));b.addActionListener(e->{activeCat=cat;refreshCatBtns();showSection("catalog");refresh();});return b;}
    private JButton actBtn(String lbl,Runnable r){JButton b=new JButton(lbl){@Override protected void paintComponent(Graphics g){Graphics2D g2=(Graphics2D)g.create();g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);g2.setColor(getModel().isRollover()?new Color(35,46,78):new Color(26,33,55));g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),8,8));super.paintComponent(g);g2.dispose();}};b.setFont(F_SMALL);b.setForeground(SUBTEXT);b.setHorizontalAlignment(SwingConstants.LEFT);b.setBorder(new EmptyBorder(7,13,7,8));b.setMaximumSize(new Dimension(Integer.MAX_VALUE,32));b.setAlignmentX(LEFT_ALIGNMENT);b.setOpaque(false);b.setContentAreaFilled(false);b.setBorderPainted(false);b.setFocusPainted(false);b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));b.addActionListener(e->r.run());return b;}
    private JComboBox<String> makeCombo(String[] items){JComboBox<String> cb=new JComboBox<>(items);cb.setFont(F_BODY);cb.setBackground(new Color(24,32,56));cb.setForeground(TEXT);cb.setBorder(BorderFactory.createLineBorder(BORDER));cb.setRenderer(new DefaultListCellRenderer(){@Override public Component getListCellRendererComponent(JList<?> l,Object v,int i,boolean s,boolean f){JLabel lbl=(JLabel)super.getListCellRendererComponent(l,v,i,s,f);lbl.setBackground(s?ACCENT:CARD);lbl.setForeground(s?Color.WHITE:TEXT);lbl.setFont(F_BODY);lbl.setBorder(new EmptyBorder(5,10,5,10));return lbl;}});return cb;}
    private JLabel navLbl(String t){JLabel l=new JLabel(t);l.setFont(F_SMALL);l.setForeground(TEXT);l.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));return l;}
    private MouseAdapter navClick(Runnable r){return new MouseAdapter(){@Override public void mouseClicked(MouseEvent e){r.run();}};}
    private JLabel sep(){JLabel l=new JLabel("|");l.setFont(F_SMALL);l.setForeground(MUTED);return l;}
    private JLabel sbHdr(String t){JLabel l=new JLabel(t);l.setFont(new Font("Segoe UI",Font.BOLD,10));l.setForeground(MUTED);l.setBorder(new EmptyBorder(0,8,4,0));l.setAlignmentX(LEFT_ALIGNMENT);return l;}
    private JLabel sLine(String k,String v,Color c){JLabel l=new JLabel(k+"   "+v);l.setFont(F_BOLD);l.setForeground(c);l.setAlignmentX(LEFT_ALIGNMENT);return l;}
    private JPanel secHdr(String title,Runnable back){JPanel h=new JPanel(new BorderLayout(10,0));h.setBackground(CARD);h.setBorder(new EmptyBorder(10,18,10,18));JLabel t=new JLabel(title);t.setFont(F_H2);t.setForeground(TEXT);JButton b=smBtn("Back to Catalog",CARD2);b.setForeground(ACCENT);b.addActionListener(e->back.run());h.add(t,BorderLayout.WEST);h.add(b,BorderLayout.EAST);return h;}
    private JLabel pillLbl(String cat){JLabel l=new JLabel("  "+cat+"  "){@Override protected void paintComponent(Graphics g){Graphics2D g2=(Graphics2D)g.create();g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);g2.setColor(catCol(cat));g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),8,8));super.paintComponent(g);g2.dispose();}};l.setFont(F_SMALL);l.setForeground(Color.WHITE);l.setOpaque(false);l.setBorder(new EmptyBorder(2,4,2,4));return l;}
    private JPanel dCell(String k,String v){JPanel p=new JPanel(new GridLayout(2,1,0,2));p.setOpaque(false);JLabel kl=new JLabel(k);kl.setFont(F_SMALL);kl.setForeground(MUTED);JLabel vl=new JLabel(v);vl.setFont(F_BOLD);vl.setForeground(TEXT);p.add(kl);p.add(vl);return p;}
    private JLabel iRow(String k,String v){JLabel l=new JLabel(k+":  "+v);l.setFont(F_BODY);l.setForeground(TEXT);l.setAlignmentX(LEFT_ALIGNMENT);return l;}
    private JPanel srchWrap(){return new JPanel(new BorderLayout()){@Override protected void paintComponent(Graphics g){Graphics2D g2=(Graphics2D)g.create();g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);g2.setColor(new Color(255,255,255,20));g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),10,10));g2.setColor(new Color(255,255,255,50));g2.draw(new RoundRectangle2D.Float(0.5f,0.5f,getWidth()-1,getHeight()-1,10,10));g2.dispose();}};} // returns opaque=false panel
    private void styleOrdTbl(JTable t){t.setFont(F_BODY);t.setRowHeight(34);t.setShowGrid(false);t.setIntercellSpacing(new Dimension(0,2));t.setBackground(ROW_A);t.setForeground(TEXT);t.setSelectionBackground(ROW_SEL);t.setSelectionForeground(Color.WHITE);t.setFocusable(false);t.getTableHeader().setReorderingAllowed(false);JTableHeader th=t.getTableHeader();th.setFont(F_H3);th.setBackground(HDR_BG);th.setForeground(SUBTEXT);th.setPreferredSize(new Dimension(0,38));th.setBorder(BorderFactory.createMatteBorder(0,0,1,0,BORDER));th.setDefaultRenderer(new DefaultTableCellRenderer(){@Override public Component getTableCellRendererComponent(JTable tbl,Object v,boolean s,boolean f,int r,int c){JLabel l=(JLabel)super.getTableCellRendererComponent(tbl,v,s,f,r,c);l.setBackground(HDR_BG);l.setForeground(SUBTEXT);l.setFont(F_H3);l.setBorder(new EmptyBorder(6,12,6,12));return l;}});}
    private Color catCol(String c){switch(c){case "Electronics":return new Color(59,130,246,170);case "Clothing":return new Color(168,85,247,170);case "Books":return new Color(180,140,0,170);case "Home & Garden":return new Color(34,197,94,170);case "Sports":return new Color(239,68,68,170);case "Beauty":return new Color(220,72,153,170);default:return new Color(100,100,140,170);}}
    private String catInit(String c){switch(c){case "Electronics":return "EC";case "Clothing":return "CL";case "Books":return "BK";case "Home & Garden":return "HG";case "Sports":return "SP";case "Beauty":return "BT";default:return "PR";}}
    private String henc(String s){return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;");}
}