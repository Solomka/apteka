package com.upp.apteka.activity;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.upp.apteka.bo.Purchase;
import com.upp.apteka.config.Mapper;
import com.upp.apteka.service.PurchaseService;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;

@Component("allPurchasesActivity")
@Scope("prototype")
public class AllPurchases implements Activity {

	@Autowired
	private Mapper mapper;

	@Autowired
	private JFrame jFrame;

	private JTable purchasesTable;

	private Object[] columnsHeader = new String[] { "Номер", "Дата", "Пацієнт", "Аптека", "Рецепт" };

	private JTextField queryField;

	private static final int BUTTON_HEIGHT = 20;
	private static final int BUTTON_WIDTH = 120;

	private static final int PAGINATION_BUTTON_HEIGHT = 25;
	private static final int PAGINATION_BUTTON_WIDTH = 90;

	private static final int ROW_HEIGHT = 32;

	private static final Font font = new Font("SansSerif", Font.PLAIN, 14);

	private List<Purchase> purchases;

	private int lastPage;
	private int currentPage;
	
	private Date startDate;
	private Date endDate;

	private String query;
	
	private UtilDateModel endModel;
	private UtilDateModel startModel;

	@Autowired
	private PurchaseService purchaseService;

	@SuppressWarnings("unchecked")
	//@Override
	public void showActivity(final Map<String, Object> params) {

		JPanel mainPanel = new JPanel();
		mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

		jFrame.setContentPane(mainPanel);

		purchases = (List<Purchase>) params.get("purchases");
		jFrame.setLayout(new BorderLayout());

		lastPage = (Integer) params.get("last");
		currentPage = (Integer) params.get("current");
		startDate = (Date) params.get("startDate");
		endDate = (Date) params.get("endDate");

		JPanel searchPanel = new JPanel();
		searchPanel.setLayout(new BorderLayout());
		searchPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		JPanel fieldWrapper = new JPanel();
		queryField = new JTextField();

		JButton queryButton = new JButton("Шукати");
		queryButton.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
		queryField.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT - 5));
		
		startModel = new UtilDateModel();
		endModel = new UtilDateModel();

		queryButton.addActionListener(new ActionListener() {

			//@Override
			public void actionPerformed(ActionEvent e) {
				params.put("query", queryField.getText());
				params.put("startDate", startModel.getValue());
				params.put("endDate", endModel.getValue());
				mapper.changeActivity("allPurchases", params);
			}
		});

		query = (String) params.get("query");
		queryField.setText(query);
		
		fieldWrapper.setLayout(new GridLayout(0, 1));
		fieldWrapper.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
		fieldWrapper.add(queryField);

		JPanel contPanel = new JPanel();
		contPanel.setLayout(new FlowLayout());

		JPanel emptyPanel = new JPanel();
		emptyPanel.setPreferredSize(new Dimension(30, BUTTON_HEIGHT));
		searchPanel.add(emptyPanel, BorderLayout.WEST);

		JLabel infoLabel = new JLabel("Сторінка: " + currentPage + "/" + lastPage);
		infoLabel.setFont(font);

		contPanel.add(infoLabel);
		contPanel.add(emptyPanel);

		searchPanel.add(contPanel, BorderLayout.WEST);
		searchPanel.add(fieldWrapper, BorderLayout.CENTER);
		searchPanel.add(queryButton, BorderLayout.EAST);
		
		if(startDate != null && endDate != null){
			Calendar cal = Calendar.getInstance();
			cal.setTime(endDate);
			int month = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_MONTH);
			int year = cal.get(Calendar.YEAR);
			endModel.setDate(year, month, day);
			endModel.setSelected(true);
			
			cal.setTime(startDate);
			month = cal.get(Calendar.MONTH);
			day = cal.get(Calendar.DAY_OF_MONTH);
			year = cal.get(Calendar.YEAR);
			startModel.setDate(year, month, day);
			startModel.setSelected(true);
		}

		JDatePanelImpl startDatePanel = new JDatePanelImpl(startModel);
		JDatePickerImpl startDatePicker = new JDatePickerImpl(startDatePanel);

		JDatePanelImpl endDatePanel = new JDatePanelImpl(endModel);
		JDatePickerImpl endDatePicker = new JDatePickerImpl(endDatePanel);
		
		JPanel datePanel = new JPanel();
		datePanel.setLayout(new FlowLayout());
		
		JLabel startLabel = new JLabel("Початок: ");
		startLabel.setFont(font);
		datePanel.add(startLabel);
		datePanel.add(startDatePicker);
		
		JLabel endLabel = new JLabel("Кінець: ");
		endLabel.setFont(font);
		datePanel.add(endLabel);
		datePanel.add(endDatePicker);
		
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());
		topPanel.add(datePanel, BorderLayout.NORTH);
		topPanel.add(searchPanel, BorderLayout.SOUTH);
		
		jFrame.add(topPanel, BorderLayout.NORTH);

		purchasesTable = new JTable(getData(purchases), columnsHeader);
		purchasesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		purchasesTable.setFont(font);
		purchasesTable.setRowHeight(ROW_HEIGHT);
		purchasesTable.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		purchasesTable.getTableHeader().setBorder(BorderFactory.createLineBorder(Color.GRAY));
		
		purchasesTable.getColumnModel().getColumn(0).setMaxWidth(50);
		purchasesTable.getColumnModel().getColumn(0).setPreferredWidth(50);
		purchasesTable.getColumnModel().getColumn(1).setMaxWidth(85);
		purchasesTable.getColumnModel().getColumn(1).setPreferredWidth(85);

		JScrollPane scrollPane = new JScrollPane(purchasesTable);
		scrollPane.setBorder(BorderFactory.createLineBorder(jFrame.getBackground()));
		jFrame.add(scrollPane, BorderLayout.CENTER);

		JPanel paginationPanel = new JPanel();
		paginationPanel.setLayout(new BorderLayout());

		JButton nextButton = new JButton("Далі");
		nextButton.setPreferredSize(new Dimension(PAGINATION_BUTTON_WIDTH, PAGINATION_BUTTON_HEIGHT));

		nextButton.addActionListener(new ActionListener() {

			//@Override
			public void actionPerformed(ActionEvent e) {
				params.put("current", currentPage + 1);
				mapper.changeActivity("allPurchases", params);
			}
		});

		if (currentPage == lastPage)
			nextButton.setEnabled(false);

		JButton prevButton = new JButton("Назад");
		prevButton.setPreferredSize(new Dimension(PAGINATION_BUTTON_WIDTH, PAGINATION_BUTTON_HEIGHT));

		prevButton.addActionListener(new ActionListener() {

			//@Override
			public void actionPerformed(ActionEvent e) {
				params.put("current", currentPage - 1);
				mapper.changeActivity("allPurchases", params);
			}
		});

		if (currentPage == 1)
			prevButton.setEnabled(false);

		JPanel goPanel = new JPanel();
		final JTextField goTo = new JTextField(10);
		goPanel.setLayout(new GridLayout(0, 1));
		goPanel.setPreferredSize(new Dimension(PAGINATION_BUTTON_WIDTH, PAGINATION_BUTTON_HEIGHT));
		goPanel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
		goPanel.add(goTo);

		JButton editButton = new JButton("Змінити");
		editButton.setPreferredSize(new Dimension(PAGINATION_BUTTON_WIDTH, PAGINATION_BUTTON_HEIGHT));

		editButton.addActionListener(new ActionListener() {

			//@Override
			public void actionPerformed(ActionEvent e) {
				int selectedRow = purchasesTable.getSelectedRow();

				if (selectedRow != -1) {
					Long id = purchases.get(selectedRow).getId();

					Map<String, Object> params = new HashMap<String, Object>();
					params.put("id", id);

					mapper.changeActivity("addPurchase", params);
				} else {
					JOptionPane.showMessageDialog(jFrame, new String[] { "Виберіть спочатку покупку!" }, "Помилка",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		JButton viewButton = new JButton("Деталі");
		viewButton.setPreferredSize(new Dimension(PAGINATION_BUTTON_WIDTH, PAGINATION_BUTTON_HEIGHT));
		viewButton.addActionListener(new ActionListener() {

			//@Override
			public void actionPerformed(ActionEvent e) {
				int selectedRow = purchasesTable.getSelectedRow();

				if (selectedRow != -1) {
					Long id = purchases.get(selectedRow).getId();

					Map<String, Object> params = new HashMap<String, Object>();
					params.put("id", id);

					mapper.changeActivity("viewPurchase", params);
				} else {
					JOptionPane.showMessageDialog(jFrame, new String[] { "Виберіть спочатку покупку!" }, "Помилка",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		JButton removeButton = new JButton("Видалити");
		removeButton.setPreferredSize(new Dimension(PAGINATION_BUTTON_WIDTH, PAGINATION_BUTTON_HEIGHT));

		removeButton.addActionListener(new ActionListener() {

			//@Override
			public void actionPerformed(ActionEvent e) {
				int selectedRow = purchasesTable.getSelectedRow();

				if (selectedRow != -1) {
					Long id = purchases.get(selectedRow).getId();

					boolean success =  purchaseService.delete(id);

					if (success)
						mapper.changeActivity("allPurchases", params);
					else {
						JOptionPane.showMessageDialog(jFrame, new String[] { "Щось пішло не так. Спробуйте пізніше." },
								"Помилка", JOptionPane.ERROR_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(jFrame, new String[] { "Виберіть спочатку покупку!" }, "Помилка",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		JButton goButton = new JButton("Перейти");
		goButton.setPreferredSize(new Dimension(PAGINATION_BUTTON_WIDTH, PAGINATION_BUTTON_HEIGHT));
		goButton.addActionListener(new ActionListener() {
			
			//@Override
			public void actionPerformed(ActionEvent e) {
				try{
					int page = Integer.valueOf(goTo.getText());
					
					if(page > lastPage)
						page = lastPage;
					
					if(page < 0)
						page = 0;
					
					params.put("current", page);
					mapper.changeActivity("allPurchases", params);
				}catch(Exception ex){
					JOptionPane.showMessageDialog(jFrame, new String[] { "Числа нормальні треба вводити!" }, "Помилка",
							JOptionPane.ERROR_MESSAGE);
				}
				
			}
		});

		paginationPanel.setLayout(new FlowLayout());
		paginationPanel.add(nextButton, BorderLayout.WEST);
		paginationPanel.add(goPanel, BorderLayout.WEST);
		paginationPanel.add(goButton, BorderLayout.WEST);
		paginationPanel.add(prevButton, BorderLayout.WEST);
		paginationPanel.add(editButton, BorderLayout.WEST);
		paginationPanel.add(removeButton, BorderLayout.WEST);
		paginationPanel.add(viewButton, BorderLayout.WEST);

		jFrame.add(paginationPanel, BorderLayout.SOUTH);
	}

	private Object[][] getData(List<Purchase> purchases) {
		Object[][] array = new Object[purchases.size()][columnsHeader.length];

		for (int i = 0; i < purchases.size(); i++) {
			array[i][0] = purchases.get(i).getId();
			array[i][1] = purchases.get(i).getDate();
			array[i][2] = purchases.get(i).getPrescription().getPatient().getSurname() + " " + purchases.get(i).getPrescription().getPatient().getName();
			array[i][3] = purchases.get(i).getPharmacy().getName();
			array[i][4] = purchases.get(i).getPrescription().getDate() + " " + purchases.get(i).getPrescription().getDoctor().getSurname();
		}

		return array;
	}
}
