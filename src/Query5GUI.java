package Query5;


import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import Query5.Basket.Basket;
import Query5.Basket.BasketList;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.awt.event.ActionEvent;

public class Query5GUI extends JFrame {

	private JPanel contentPane;
	private JTable table;
	private DefaultTableModel tableM;
	JTextField upcTf;
	private JTextField amountTf;
	Connection connection = getConnection();
	Statement statement = connection.createStatement();
	ArrayList<Basket> basket = new ArrayList<Basket>();
	
	// 店號列表
	private int[] storeIdList = { 14342, 16592, 18281,18378,18623, 18659, 19039, 19775, 20374, 20445 };
	private JTextField memberTf;



	/**
	 * Create the frame.
	 * @throws SQLException 
	 */
	public Query5GUI() throws SQLException {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			System.out.println("Driver loaded!");
			
		} catch (ClassNotFoundException e) {
			System.out.println("找不到驅動程式類別");
			e.printStackTrace();
		}
		Query5 q5 = new Query5(statement);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 658, 446);
		contentPane = new JPanel();
		contentPane.setLayout(null);
		setContentPane(contentPane);

		JLabel memberLb = new JLabel("會員編號(如:1,2,3...,非會員則免填):");
		memberLb.setFont(new Font("微軟正黑體", Font.PLAIN, 16));
		memberLb.setBounds(53, 4, 277, 45);
		contentPane.add(memberLb);

		memberTf = new JTextField();
		memberTf.setColumns(10);
		memberTf.setBounds(311, 11, 187, 31);
		contentPane.add(memberTf);

		JLabel storeLb = new JLabel("分店:");
		storeLb.setFont(new Font("微軟正黑體", Font.PLAIN, 16));
		storeLb.setBounds(115, 48, 51, 45);
		contentPane.add(storeLb);

		JButton search = new JButton("查詢");
		search.setBounds(413, 59, 85, 23);
		contentPane.add(search);

		JLabel upcLb = new JLabel("產品UPC:");
		upcLb.setFont(new Font("微軟正黑體", Font.PLAIN, 16));
		upcLb.setBounds(53, 93, 92, 45);
		contentPane.add(upcLb);

		upcTf = new JTextField();
		upcTf.setBounds(140, 100, 132, 31);
		contentPane.add(upcTf);
		upcTf.setColumns(10);

		JLabel amountLb = new JLabel("數量:");
		amountLb.setFont(new Font("微軟正黑體", Font.PLAIN, 16));
		amountLb.setBounds(294, 92, 75, 45);
		contentPane.add(amountLb);

		amountTf = new JTextField();
		amountTf.setColumns(10);
		amountTf.setBounds(343, 99, 132, 31);
		contentPane.add(amountTf);

		JButton buyBtn = new JButton("加入購物車");
		buyBtn.setBounds(488, 108, 122, 23);
		contentPane.add(buyBtn);

		// 選單列表
		String[] list = { "14342 新屋車站店", "16592 屏東中華店", "18281 台中成功店", "18378 台中高鐵店", "18623 楊梅車站店", "18659 燕巢興師大店", "19039 台南國安店", "19775 屏東正裕店", "20374 屏東大崇店", "20445 台南赤崁店"};
		JComboBox<String> storeBox = new JComboBox<String>(list);
    //JComboBox<String> storeBox = new JComboBox<String>();
		storeBox.setBounds(176, 58, 216, 25);
		contentPane.add(storeBox);

		String[] title = { "UPC", "品牌", "商品名稱", "大小", "price", "庫存數量"  }; // 標題
		tableM = new DefaultTableModel(null, title);// table內容
		table = new JTable(tableM) {
			public boolean isCellEditable(int row, int column) {
				return false;
			}// 表格不允許被編輯
		};
		JScrollPane tablePane = new JScrollPane(table);// 滑動滾輪與顯示標題列
		tablePane.setBounds(22, 169, 599, 217);
		contentPane.add(tablePane);

		JButton checkOut = new JButton("結帳");

		checkOut.setBounds(264, 141, 85, 23);
		contentPane.add(checkOut);
		
		JButton checkBasket = new JButton("查看購物車");
		checkBasket.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new BasketList(basket);
			}
		});
		checkBasket.setBounds(519, 4, 97, 94);
		contentPane.add(checkBasket);
		repaint();

		// 按下查詢按鈕
		search.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cleanTable(tableM);// 清空menu
				updateMenu(storeBox.getSelectedIndex());// 更新menu
				repaint();
			}
		});
		
		//按下加入購物車按鈕
		buyBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int storeID = storeIdList[storeBox.getSelectedIndex()];
				int buyAmount= Integer.parseInt(amountTf.getText());
				String upc = upcTf.getText();
				String check =
						"SELECT amount FROM `inventory`"
						+" WHERE UPC = "+upcTf.getText()
						+" AND   store_id ="+storeID;
				boolean upcCorrect = false;
				try (ResultSet resultSet = statement.executeQuery(check)) {
					while (resultSet.next()) {
						upcCorrect = true;
						int recentAmount = Integer.parseInt(resultSet.getString("amount"));
						if(recentAmount<buyAmount)
						   JOptionPane.showMessageDialog(new JFrame(),"加入失敗，購買數量大於庫存，請減少購買數量",
	                                  "加入失敗", JOptionPane.INFORMATION_MESSAGE);
						else if(buyAmount<1)
							   JOptionPane.showMessageDialog(new JFrame(),"加入失敗，不可購買小於1數量的商品",
		                                  "加入失敗", JOptionPane.INFORMATION_MESSAGE);
						else {
							basket.add(new Basket(
									upc,buyAmount));
							upcTf.setText("");
						}
					}
					if(!upcCorrect) {
						   JOptionPane.showMessageDialog(new JFrame(),"本商店並無提供UPC:"+upcTf.getText()+"的商品",
	                                  "加入失敗", JOptionPane.INFORMATION_MESSAGE);
							upcTf.setText("");
					}
					amountTf.setText("");
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		
		//按下結帳按鈕
		checkOut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int storeID = storeIdList[storeBox.getSelectedIndex()];
				if("".equals(memberTf.getText())) {
					try {
						q5.checkOut(upcTf,storeID, basket, null);
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
				else {
					try {
						q5.checkOut(upcTf,storeID, basket, Integer.parseInt(memberTf.getText()));
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
				memberTf.setText("");
				basket.clear();
			}
		});
	}

	void updateMenu(int index) {
		int storeID = storeIdList[index];
		try {

			String discover = "SELECT p.UPC, brand, name, size, price, amount " 
							+ " FROM inventory i, product p"
							+ "	WHERE store_id = "+storeID
							+ "	AND i.UPC = p.UPC";
			try (ResultSet resultSet = statement.executeQuery(discover)) {
				while (resultSet.next()) {
					Object[]temp = {resultSet.getString("p.UPC"),
									resultSet.getString("brand"),
									resultSet.getString("name"),
									resultSet.getString("size"),
									resultSet.getString("price"),
									resultSet.getString("amount")};
					tableM.addRow(temp);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	void cleanTable(DefaultTableModel tableM) {
		while (tableM.getRowCount() > 0)
			tableM.removeRow(0);
	}

	private Connection getConnection() throws SQLException {
		String serverName = "serverName_URL"; // 伺服器網址
		String database = "db_name"; // 資料庫名稱
		String user = "user_name"; // 使用者名稱
		String password = "user_pwd"; // 使用者密碼
		String url = "jdbc:mysql://" + serverName + "/" + database; // JDBC線
		return DriverManager.getConnection(url, user, password); // 連線方法
	}
}
