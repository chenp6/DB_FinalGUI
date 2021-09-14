package Query5.Basket;


import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;


public class BasketList extends JFrame {

	public JPanel contentPane;

	public BasketList(ArrayList<Basket>basket) {
		super();
		setVisible(true);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 400, 463);
		contentPane = new JPanel();
		contentPane.setLayout(null);
		setContentPane(contentPane);
		StringBuilder str = new StringBuilder();
		String[] title = { "UPC", "數量"}; // 標題
		DefaultTableModel tableM = new DefaultTableModel(null, title);// table內容
		JTable table = new JTable(tableM) {
			public boolean isCellEditable(int row, int column) {
				return false;
			}// 表格不允許被編輯
		};
		JScrollPane tablePane = new JScrollPane(table);// 滑動滾輪與顯示標題列
		tablePane.setBounds(22, 37, 344, 349);
		for(int i=0;i<basket.size();i++) {
			tableM.addRow(new Object[] {basket.get(i).UPC,basket.get(i).amount});
		}
		contentPane.add(tablePane);
	}

}
