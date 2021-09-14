package Query5;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import Query5.Basket.Basket;


public class Query5 {
	private Statement statement;

	Query5(Statement statement) {
		this.statement = statement;
	}

	public void checkOut(JTextField upcTf,int storeID, ArrayList<Basket> basket, Integer memberID) throws SQLException {

		String invoice = produceInvoice();
		String time = getDate();
		System.out.println(invoice+" "+time);



		
		
		

		boolean emptyBasket = true;

		// 加消費者紀錄
		String addShopper;
		if (memberID == null) {
			addShopper = "INSERT INTO `shopper` (`invoice`,`store_id`,`date`, `member_id`)" + " VALUES (\'" + invoice
					+ "\',\'" + storeID + "\',\'" + time + "\',NULL);";
		} else {
			//檢查是否存在此會員編號
			String memberCheck ="SELECT count(member_id) FROM `member` WHERE member_id = "+memberID;
			try (ResultSet resultSet = statement.executeQuery(memberCheck)) {
				while (resultSet.next()) {
					int num = Integer.parseInt(resultSet.getString("count(member_id)"));
					if(num==0) {
						JOptionPane.showMessageDialog(new JFrame(), "不存在此會員編號，請重新填寫，謝謝", "提醒", JOptionPane.INFORMATION_MESSAGE);
						upcTf.setText("");
						return;
					}
						
				}
			}
			addShopper = "INSERT INTO `shopper` (`invoice`,`store_id`,`date`, `member_id`)" + " VALUES (\'" + invoice
					+ "\',\'" + storeID + "\',\'" + time + "\',\'" + memberID + "\');";
		}

		statement.executeUpdate(addShopper);

		// 減庫存、新增消費者購物車內容
		for (int i = 0; i < basket.size(); i++) {
			
			statement.executeUpdate("BEGIN; ");// 開始
			statement.executeUpdate("SAVEPOINT start");//建立儲存點
			String updateInventory = "UPDATE `inventory`" + " SET amount=amount-" + basket.get(i).amount
					+ " WHERE UPC = " + basket.get(i).UPC + " AND   store_id =" + storeID + ";";
			try {
				statement.executeUpdate(updateInventory);//更新庫存
			} catch (SQLException e) {
				// 因為庫存型態為unsigned，只要小於0就會進到catch SQLException
				statement.executeUpdate("ROLLBACK TO start");//回到儲存點(還未加入這筆資料處)
				JOptionPane.showMessageDialog(new JFrame(),
						"UPC:" + basket.get(i).UPC.toString() + "購買失敗，購買數量大於目前庫存，請稍後重新購買", "購買失敗",
						JOptionPane.INFORMATION_MESSAGE);
				statement.executeUpdate("COMMIT;");//提交
				continue;
			}
			String updateBasket = "INSERT INTO `basket` (`invoice`,`UPC`,`amount`)" 
						+ " VALUES (\'" + invoice + "\',\'"+basket.get(i).UPC+"\',\'"+ basket.get(i).amount + "\');";
			try {
				statement.executeUpdate(updateBasket);//更新購物紀錄
				emptyBasket = false;
			} catch (SQLException e) {
				e.printStackTrace();
				statement.executeUpdate("ROLLBACK TO start");//回到儲存點(還未加入這筆資料處)
			}
			statement.executeUpdate("COMMIT;");//提交

		}

		if (emptyBasket == true) {
			//刪除這筆shopper顧客紀錄
			String delShopper = "DELETE FROM `shopper` WHERE `shopper`.`invoice` = \'"+invoice+"\';";
			statement.executeUpdate(delShopper);
			
			JOptionPane.showMessageDialog(new JFrame(), "您未成功購買任何商品，所以不會產生發票喔", "提醒", JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(new JFrame(), "<html><body>您的發票:" + invoice + "<br>謝謝光臨</body></html>", "發票產生",JOptionPane.INFORMATION_MESSAGE);
		}


	}

	private String produceInvoice() {
		Random r = new Random();
		int upCase1 = r.nextInt(26) + 65;
		int upCase2 = r.nextInt(26) + 65;
		String invoice = String.valueOf((char) upCase1) + String.valueOf((char) upCase2);
		for (int i = 0; i < 8; i++) {
			Integer num = r.nextInt(9);
			invoice += num.toString();
		}
		return invoice;
	}

	private String getDate() {
		Date date = new Date();
		SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = sdFormat.format(date).toString();
		return time;
	}



}