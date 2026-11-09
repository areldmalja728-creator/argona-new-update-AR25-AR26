import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ImageIcon;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

public class argona extends JFrame {
	private static final Path ACCOUNTS_FILE = Paths.get("accounts.properties");
	private static final Color BG = new Color(8, 12, 20);
	private static final Color PANEL = new Color(14, 20, 30);
	private static final Color CARD = new Color(18, 24, 36);
	private static final Color CARD_ALT = new Color(24, 32, 46);
	private static final Color INPUT = new Color(30, 39, 54);
	private static final Color TEXT = new Color(245, 247, 251);
	private static final Color MUTED = new Color(153, 169, 187);
	private static final Color ACCENT = new Color(110, 92, 255);
	private static final Color ACCENT_2 = new Color(36, 201, 168);
	private static final Color HOVER = new Color(132, 118, 255);
	private static final Color ERROR = new Color(248, 113, 113);
	private static final Color SUCCESS = new Color(74, 222, 128);
	private static final Color BORDER = new Color(43, 58, 79);

	private static final String ROLE_PREFIX = "role.";
	private static final String BANNED_PREFIX = "banned.";
	private static final String LANGUAGE_PREFIX = "language.";
	private static final String LAST_ACCOUNT_KEY = "last.account";
	private static final String DEFAULT_ADMIN = "argona";
	private static final String SPONSORED_AD_URL = "https://www.microsoft.com/";

	private final JTextField username = new JTextField();
	private final JPasswordField password = new JPasswordField();
	private final JLabel message = new JLabel(" ");
	private final Properties accounts = loadAccounts();
	private final List<String> createdPosts = new ArrayList<>();
	private final List<String> createdVideos = new ArrayList<>();
	private final List<String> createdStories = new ArrayList<>();
	private String verifiedAdminAccount = null;
	private javax.swing.JTabbedPane appTabs;
	private String currentAccount;
	private boolean premiumBlueTheme;
	private int ar29UpdateCount;
	private String selectedLanguage;
	private static final String[] ARGONA_VERSIONS = {
		"AR4", "AR5", "AR6", "AR7", "AR8", "ARX", "AR10", "AR11", "AR12", "AR13",
		"AR14", "AR15", "AR16", "AR17", "AR18", "AR19", "AR20", "AR21", "AR22", "AR23",
		"AR24", "AR25", "AR26", "AR27", "AR28", "AR29"
	};
	private int currentVersionIndex = ARGONA_VERSIONS.length - 1;
	private JLabel appVersionLabel;

	public argona() {
		super("Argona | Login");
		if (!isJavaInstalled()) {
			javax.swing.JOptionPane.showMessageDialog(this,
					"Please install Java to update Argona for a better update experience.",
					"Java Required",
					javax.swing.JOptionPane.WARNING_MESSAGE);
		}
		setIconImage(createAppIcon());
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent event) {
				showExitNotification();
			}
		});
		setSize(1100, 760);
		setMinimumSize(new Dimension(900, 650));
		setLocationRelativeTo(null);
		saveAccounts();
		setContentPane(buildPage());
		String rememberedAccount = accounts.getProperty(LAST_ACCOUNT_KEY);
		if (rememberedAccount != null && accounts.getProperty(rememberedAccount) != null
				&& !isBannedAccount(rememberedAccount)) {
			openHome(rememberedAccount);
		}
	}

	private boolean isJavaInstalled() {
		String javaHome = System.getProperty("java.home");
		return javaHome != null && !javaHome.trim().isEmpty();
	}

	private Image createAppIcon() {
		int size = 64;
		BufferedImage icon = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = icon.createGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setColor(Color.BLACK);
		graphics.fillRoundRect(2, 2, size - 4, size - 4, 14, 14);
		graphics.setColor(Color.WHITE);
		graphics.setFont(new Font("SansSerif", Font.BOLD, 30));
		String letter = "A";
		int textWidth = graphics.getFontMetrics().stringWidth(letter);
		int textBaseline = (size - graphics.getFontMetrics().getHeight()) / 2 + graphics.getFontMetrics().getAscent();
		graphics.drawString(letter, (size - textWidth) / 2, textBaseline);
		graphics.dispose();
		return icon;
	}

	private JPanel buildPage() {
		JPanel page = new JPanel(new GridBagLayout());
		page.setBackground(new Color(6, 10, 18));

		JPanel card = new JPanel(new BorderLayout(0, 18));
		card.setPreferredSize(new Dimension(440, 520));
		card.setOpaque(true);
		card.setBackground(CARD);
		card.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(BORDER, 1),
				BorderFactory.createEmptyBorder(30, 32, 22, 32)));
		card.add(buildHeader(), BorderLayout.NORTH);
		card.add(buildForm(), BorderLayout.CENTER);
		card.add(buildFooter(), BorderLayout.SOUTH);

		page.add(card);
		return page;
	}

	private JPanel buildHeader() {
		JPanel header = verticalPanel();
		JLabel logo = new JLabel("A", JLabel.CENTER);
		logo.setOpaque(true);
		logo.setBackground(Color.BLACK);
		logo.setForeground(Color.WHITE);
		logo.setFont(new Font("SansSerif", Font.BOLD, 30));
		logo.setPreferredSize(new Dimension(64, 64));
		logo.setMaximumSize(new Dimension(64, 64));
		logo.setAlignmentX(0.5f);

		JLabel title = text("Welcome back", 30, TEXT, Font.BOLD);
		title.setAlignmentX(0.5f);
		JLabel subtitle = text("Sign in to continue to Argona", 14, MUTED, Font.PLAIN);
		subtitle.setAlignmentX(0.5f);

		appVersionLabel = new JLabel(ARGONA_VERSIONS[currentVersionIndex], JLabel.CENTER);
		appVersionLabel.setOpaque(true);
		appVersionLabel.setBackground(new Color(18, 24, 36));
		appVersionLabel.setForeground(new Color(110, 92, 255));
		appVersionLabel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(BORDER, 1),
				BorderFactory.createEmptyBorder(8, 14, 8, 14)));
		appVersionLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
		appVersionLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		appVersionLabel.setAlignmentX(0.5f);
		appVersionLabel.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent event) {
				currentVersionIndex = (currentVersionIndex + 1) % ARGONA_VERSIONS.length;
				String nextVersion = ARGONA_VERSIONS[currentVersionIndex];
				appVersionLabel.setText(nextVersion);
				setTitle("Argona | " + nextVersion + " Update");
			}
		});

		header.add(logo);
		header.add(Box.createVerticalStrut(16));
		header.add(title);
		header.add(Box.createVerticalStrut(8));
		header.add(subtitle);
		header.add(Box.createVerticalStrut(10));
		header.add(appVersionLabel);
		return header;
	}

	private JPanel buildForm() {
		JPanel form = new JPanel(new GridBagLayout());
		form.setOpaque(false);
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;

		c.gridy = 0;
		c.insets = new Insets(0, 0, 7, 0);
		form.add(text("Username", 12, TEXT, Font.BOLD), c);
		styleField(username);
		c.gridy = 1;
		c.insets = new Insets(0, 0, 16, 0);
		form.add(username, c);

		c.gridy = 2;
		c.insets = new Insets(0, 0, 7, 0);
		form.add(text("Password", 12, TEXT, Font.BOLD), c);
		styleField(password);
		c.gridy = 3;
		c.insets = new Insets(0, 0, 10, 0);
		form.add(password, c);

		JCheckBox show = new JCheckBox("Show password");
		show.setOpaque(false);
		show.setForeground(MUTED);
		show.setFont(new Font("SansSerif", Font.PLAIN, 12));
		show.setFocusPainted(false);
		show.addActionListener(e -> password.setEchoChar(show.isSelected() ? (char) 0 : '\u2022'));
		c.gridy = 4;
		c.insets = new Insets(0, 0, 18, 0);
		form.add(show, c);

		JButton signIn = new JButton("Sign in");
		signIn.setPreferredSize(new Dimension(320, 48));
		signIn.setBackground(ACCENT);
		signIn.setForeground(Color.WHITE);
		signIn.setFont(new Font("SansSerif", Font.BOLD, 14));
		signIn.setFocusPainted(false);
		signIn.setBorder(BorderFactory.createEmptyBorder());
		signIn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		signIn.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent e) { signIn.setBackground(HOVER); }
			public void mouseExited(java.awt.event.MouseEvent e) { signIn.setBackground(ACCENT); }
		});
		signIn.addActionListener(e -> login());
		c.gridy = 5;
		c.insets = new Insets(0, 0, 8, 0);
		form.add(signIn, c);

		message.setFont(new Font("SansSerif", Font.PLAIN, 12));
		message.setForeground(ERROR);
		c.gridy = 6;
		form.add(message, c);
		getRootPane().setDefaultButton(signIn);
		return form;
	}

	private JPanel buildFooter() {
		JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
		footer.setOpaque(false);
		JLabel text = text("New to Argona?", 12, MUTED, Font.PLAIN);
		JButton createAccount = new JButton("Create account");
		createAccount.setForeground(new Color(165, 180, 252));
		createAccount.setFont(new Font("SansSerif", Font.BOLD, 12));
		createAccount.setContentAreaFilled(false);
		createAccount.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 2));
		createAccount.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		createAccount.addActionListener(e -> createAccount());
		footer.add(text);
		footer.add(createAccount);
		return footer;
	}

	private void styleField(JTextField field) {
		field.setPreferredSize(new Dimension(320, 46));
		field.setBackground(INPUT);
		field.setForeground(TEXT);
		field.setCaretColor(TEXT);
		field.setFont(new Font("SansSerif", Font.PLAIN, 14));
		field.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(BORDER, 1),
				BorderFactory.createEmptyBorder(0, 14, 0, 14)));
	}

	private void login() {
		String enteredUsername = username.getText().trim();
		String enteredPassword = new String(password.getPassword());
		if (enteredUsername.isEmpty() || enteredPassword.isEmpty()) {
			showMessage("Please enter both username and password.", ERROR);
		} else if (accounts.getProperty(enteredUsername) == null) {
			showMessage("Incorrect username or password.", ERROR);
			password.selectAll();
			password.requestFocusInWindow();
		} else if (isBannedAccount(enteredUsername)) {
			showMessage("This account is banned.", ERROR);
			password.selectAll();
			password.requestFocusInWindow();
		} else if (accounts.getProperty(enteredUsername).equals(enteredPassword)) {
			if (isAdminAccount(enteredUsername) && !requireVerifiedAdmin(enteredUsername)) {
				return;
			}
			openHome(enteredUsername);
		} else {
			showMessage("Incorrect username or password.", ERROR);
			password.selectAll();
			password.requestFocusInWindow();
		}
	}

	private boolean isAdminAccount(String accountName) {
		return "admin".equalsIgnoreCase(roleFor(accountName));
	}

	private boolean isBannedAccount(String accountName) {
		return Boolean.parseBoolean(accounts.getProperty(BANNED_PREFIX + accountName, "false"));
	}

	private String roleFor(String accountName) {
		return accounts.getProperty(ROLE_PREFIX + accountName, "user");
	}

	private boolean isAdminVerified(String accountName) {
		return accountName != null && accountName.equals(verifiedAdminAccount);
	}

	private boolean verifyHumanCheck() {
		JCheckBox notRobot = new JCheckBox("I am not a robot");
		JCheckBox notHacker = new JCheckBox("I am not a hacker");
		JTextField humanText = new JTextField();
		humanText.setText("");
		javax.swing.JPanel panel = new javax.swing.JPanel();
		panel.setLayout(new javax.swing.BoxLayout(panel, javax.swing.BoxLayout.Y_AXIS));
		panel.add(new JLabel("Complete the security check before admin access."));
		panel.add(Box.createVerticalStrut(8));
		panel.add(notRobot);
		panel.add(notHacker);
		panel.add(Box.createVerticalStrut(8));
		panel.add(new JLabel("Type: human"));
		panel.add(humanText);

		int result = javax.swing.JOptionPane.showConfirmDialog(this, panel,
				"Human verification", javax.swing.JOptionPane.OK_CANCEL_OPTION,
				javax.swing.JOptionPane.PLAIN_MESSAGE);
		if (result != javax.swing.JOptionPane.OK_OPTION) {
			return false;
		}
		String typed = humanText.getText() == null ? "" : humanText.getText().trim().toLowerCase();
		return notRobot.isSelected() && notHacker.isSelected() && "human".equals(typed);
	}

	private boolean verifyAdminSession(String accountName) {
		if (!isAdminAccount(accountName)) {
			javax.swing.JOptionPane.showMessageDialog(this, "This account is not an admin account.", "Access denied",
					javax.swing.JOptionPane.ERROR_MESSAGE);
			return false;
		}
		if (!verifyHumanCheck()) {
			javax.swing.JOptionPane.showMessageDialog(this, "Verification failed. You must confirm you are not a robot and not a hacker.", "Access denied",
					javax.swing.JOptionPane.ERROR_MESSAGE);
			return false;
		}
		JPasswordField verifyField = new JPasswordField(18);
		int result = javax.swing.JOptionPane.showConfirmDialog(this, verifyField,
				"Verify admin access for " + accountName, javax.swing.JOptionPane.OK_CANCEL_OPTION,
				javax.swing.JOptionPane.PLAIN_MESSAGE);
		if (result != javax.swing.JOptionPane.OK_OPTION) {
			return false;
		}
		String enteredPassword = new String(verifyField.getPassword());
		if (!accounts.getProperty(accountName).equals(enteredPassword)) {
			javax.swing.JOptionPane.showMessageDialog(this, "Incorrect password. Admin verification failed.", "Access denied",
					javax.swing.JOptionPane.ERROR_MESSAGE);
			verifiedAdminAccount = null;
			return false;
		}
		verifiedAdminAccount = accountName;
		javax.swing.JOptionPane.showMessageDialog(this, "Admin access verified.", "Verified",
				javax.swing.JOptionPane.INFORMATION_MESSAGE);
		return true;
	}

	private boolean requireVerifiedAdmin(String accountName) {
		if (!isAdminAccount(accountName)) {
			return false;
		}
		if (!isAdminVerified(accountName)) {
			return verifyAdminSession(accountName);
		}
		return true;
	}

	private List<String> listAccountNames() {
		List<String> names = new ArrayList<>();
		for (String key : accounts.stringPropertyNames()) {
			if (key.equals(LAST_ACCOUNT_KEY) || key.startsWith(ROLE_PREFIX) || key.startsWith(BANNED_PREFIX)
					|| key.startsWith(LANGUAGE_PREFIX) || key.startsWith("profileImage.")) {
				continue;
			}
			names.add(key);
		}
		names.sort(String.CASE_INSENSITIVE_ORDER);
		return names;
	}

	private String adminUserListText() {
		List<String> names = listAccountNames();
		if (names.isEmpty()) {
			return "No users found.";
		}
		StringBuilder builder = new StringBuilder();
		for (String name : names) {
			builder.append(name)
					.append(" | role: ")
					.append(roleFor(name))
					.append(isBannedAccount(name) ? " | banned" : " | active")
					.append(System.lineSeparator());
		}
		return builder.toString();
	}

	private void makeUserAdmin(String accountName, String targetUser) {
		if (targetUser == null || targetUser.trim().isEmpty()) {
			javax.swing.JOptionPane.showMessageDialog(this, "Enter a username to add as admin.", "Admin action",
					javax.swing.JOptionPane.WARNING_MESSAGE);
			return;
		}
		String cleanName = targetUser.trim();
		if (!accounts.containsKey(cleanName)) {
			javax.swing.JOptionPane.showMessageDialog(this, "User not found: " + cleanName, "Admin action",
					javax.swing.JOptionPane.WARNING_MESSAGE);
			return;
		}
		accounts.setProperty(ROLE_PREFIX + cleanName, "admin");
		saveAccounts();
		javax.swing.JOptionPane.showMessageDialog(this, cleanName + " is now an admin.", "Admin action",
				javax.swing.JOptionPane.INFORMATION_MESSAGE);
	}

	private void banUser(String accountName, String targetUser) {
		if (targetUser == null || targetUser.trim().isEmpty()) {
			javax.swing.JOptionPane.showMessageDialog(this, "Enter a username to ban.", "Admin action",
					javax.swing.JOptionPane.WARNING_MESSAGE);
			return;
		}
		String cleanName = targetUser.trim();
		if (cleanName.equalsIgnoreCase(accountName)) {
			javax.swing.JOptionPane.showMessageDialog(this, "You cannot ban your own admin account.", "Admin action",
					javax.swing.JOptionPane.WARNING_MESSAGE);
			return;
		}
		if (isAdminAccount(cleanName)) {
			javax.swing.JOptionPane.showMessageDialog(this,
					"Admin accounts are protected and cannot be banned.", "Admin action",
					javax.swing.JOptionPane.WARNING_MESSAGE);
			return;
		}
		if (!accounts.containsKey(cleanName)) {
			javax.swing.JOptionPane.showMessageDialog(this, "User not found: " + cleanName, "Admin action",
					javax.swing.JOptionPane.WARNING_MESSAGE);
			return;
		}
		accounts.setProperty(BANNED_PREFIX + cleanName, "true");
		saveAccounts();
		javax.swing.JOptionPane.showMessageDialog(this, cleanName + " has been banned.", "Admin action",
				javax.swing.JOptionPane.INFORMATION_MESSAGE);
	}

	private void kickUser(String accountName, String targetUser) {
		if (targetUser == null || targetUser.trim().isEmpty()) {
			javax.swing.JOptionPane.showMessageDialog(this, "Enter a username to kick.", "Admin action",
					javax.swing.JOptionPane.WARNING_MESSAGE);
			return;
		}
		String cleanName = targetUser.trim();
		if (cleanName.equalsIgnoreCase(accountName)) {
			javax.swing.JOptionPane.showMessageDialog(this, "You cannot kick your own admin account.", "Admin action",
					javax.swing.JOptionPane.WARNING_MESSAGE);
			return;
		}
		if (isAdminAccount(cleanName)) {
			javax.swing.JOptionPane.showMessageDialog(this,
					"Admin accounts are protected and cannot be kicked.", "Admin action",
					javax.swing.JOptionPane.WARNING_MESSAGE);
			return;
		}
		if (!accounts.containsKey(cleanName)) {
			javax.swing.JOptionPane.showMessageDialog(this, "User not found: " + cleanName, "Admin action",
					javax.swing.JOptionPane.WARNING_MESSAGE);
			return;
		}
		accounts.setProperty(BANNED_PREFIX + cleanName, "true");
		saveAccounts();
		javax.swing.JOptionPane.showMessageDialog(this,
				cleanName + " has been suspended. The account remains saved and cannot be unsaved.", "Admin action",
				javax.swing.JOptionPane.INFORMATION_MESSAGE);
	}

	private JPanel adminPanel(String accountName) {
		JPanel panel = new JPanel(new BorderLayout(18, 18));
		panel.setBackground(BG);
		panel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

		JPanel header = new JPanel(new BorderLayout(12, 12));
		header.setBackground(CARD);
		header.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(BORDER, 1),
				BorderFactory.createEmptyBorder(18, 18, 18, 18)));
		JLabel statusLabel = text("Admin verification: " + (isAdminVerified(accountName) ? "Verified" : "Pending"), 12, MUTED, Font.BOLD);
		JButton verifyButton = new JButton("Verify admin");
		verifyButton.addActionListener(e -> {
			if (requireVerifiedAdmin(accountName)) {
				statusLabel.setText("Admin verification: Verified");
			}
		});
		header.add(text("Admin console", 26, TEXT, Font.BOLD), BorderLayout.WEST);
		header.add(statusLabel, BorderLayout.CENTER);
		header.add(verifyButton, BorderLayout.EAST);

		JTextArea userList = new JTextArea(adminUserListText());
		userList.setEditable(false);
		userList.setBackground(CARD);
		userList.setForeground(TEXT);
		userList.setFont(new Font("SansSerif", Font.PLAIN, 13));
		userList.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(BORDER, 1),
				BorderFactory.createEmptyBorder(12, 12, 12, 12)));
		JScrollPane listScroll = new JScrollPane(userList);

		JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
		controls.setOpaque(false);
		JTextField targetUser = new JTextField();
		targetUser.setPreferredSize(new Dimension(210, 38));
		styleField(targetUser);
		JButton addAdmin = new JButton("Add as admin");
		JButton ban = new JButton("Ban");
		JButton kick = new JButton("Kick");

		addAdmin.addActionListener(e -> {
			if (!requireVerifiedAdmin(accountName)) {
				return;
			}
			makeUserAdmin(accountName, targetUser.getText());
			userList.setText(adminUserListText());
			statusLabel.setText("Admin verification: Verified");
		});
		ban.addActionListener(e -> {
			if (!requireVerifiedAdmin(accountName)) {
				return;
			}
			banUser(accountName, targetUser.getText());
			userList.setText(adminUserListText());
			statusLabel.setText("Admin verification: Verified");
		});
		kick.addActionListener(e -> {
			if (!requireVerifiedAdmin(accountName)) {
				return;
			}
			kickUser(accountName, targetUser.getText());
			userList.setText(adminUserListText());
			statusLabel.setText("Admin verification: Verified");
		});

		controls.add(text("User:", 12, TEXT, Font.BOLD));
		controls.add(targetUser);
		controls.add(addAdmin);
		controls.add(ban);
		controls.add(kick);

		panel.add(header, BorderLayout.NORTH);
		panel.add(listScroll, BorderLayout.CENTER);
		panel.add(controls, BorderLayout.SOUTH);
		return panel;
	}

	private void createAccount() {
		String newUsername = javax.swing.JOptionPane.showInputDialog(this, "Choose a username:", "Create account", javax.swing.JOptionPane.PLAIN_MESSAGE);
		if (newUsername == null || newUsername.trim().isEmpty()) {
			return;
		}
		newUsername = newUsername.trim();
		if (!newUsername.matches("[A-Za-z0-9_]{3,24}")) {
			showMessage("Use 3-24 letters, numbers, or underscores for your username.", ERROR);
			return;
		}
		if (accountExistsIgnoreCase(newUsername)) {
			showMessage("That username is already taken.", ERROR);
			return;
		}
		JPasswordField passwordField = new JPasswordField(18);
		int result = javax.swing.JOptionPane.showConfirmDialog(this, passwordField,
				"Choose a strong password", javax.swing.JOptionPane.OK_CANCEL_OPTION,
				javax.swing.JOptionPane.PLAIN_MESSAGE);
		if (result != javax.swing.JOptionPane.OK_OPTION) {
			return;
		}
		String newPassword = new String(passwordField.getPassword());
		if (!isStrongPassword(newPassword)) {
			showMessage("Use 8+ characters with uppercase, lowercase, number and symbol.", ERROR);
			return;
		}
		accounts.setProperty(newUsername, newPassword);
		accounts.setProperty(ROLE_PREFIX + newUsername, "user");
		saveAccounts();
		username.setText(newUsername);
		password.setText(newPassword);
		showMessage("Account saved. You can sign in now.", SUCCESS);
	}

	private boolean accountExistsIgnoreCase(String candidate) {
		for (String accountName : listAccountNames()) {
			if (accountName.equalsIgnoreCase(candidate)) {
				return true;
			}
		}
		return false;
	}

	private void openHome(String accountName) {
		currentAccount = accountName;
		selectedLanguage = accounts.getProperty(LANGUAGE_PREFIX + accountName, Locale.getDefault().getLanguage());
		accounts.setProperty(LAST_ACCOUNT_KEY, accountName);
		saveAccounts();
		getContentPane().removeAll();
		getContentPane().setLayout(new BorderLayout());
		getContentPane().setBackground(BG);

		JPanel shell = new JPanel(new BorderLayout(18, 18));
		shell.setBackground(BG);
		shell.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		JPanel topBar = new JPanel();
		topBar.setLayout(new BoxLayout(topBar, BoxLayout.X_AXIS));
		topBar.setBackground(premiumBlueTheme ? new Color(7, 25, 44) : new Color(15, 20, 30));
		topBar.setPreferredSize(new Dimension(0, 92));
		topBar.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(74, 90, 110), 1),
				BorderFactory.createEmptyBorder(14, 18, 14, 18)));
		topBar.setOpaque(true);

		JPanel brandGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
		brandGroup.setOpaque(false);
		JLabel logo = new JLabel("A", JLabel.CENTER) {
			@Override
			protected void paintComponent(Graphics graphics) {
				Graphics2D painter = (Graphics2D) graphics.create();
				painter.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				painter.setColor(premiumBlueTheme ? new Color(30, 142, 210) : new Color(110, 92, 255));
				if (premiumBlueTheme) {
					painter.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
				} else {
					painter.fillRect(0, 0, getWidth(), getHeight());
				}
				painter.dispose();
				super.paintComponent(graphics);
			}
		};
		logo.setOpaque(false);
		logo.setForeground(premiumBlueTheme ? Color.BLACK : Color.WHITE);
		logo.setFont(new Font("SansSerif", Font.BOLD, 20));
		logo.setPreferredSize(new Dimension(42, 42));
		logo.setMaximumSize(new Dimension(42, 42));
		logo.setBorder(premiumBlueTheme
				? BorderFactory.createLineBorder(new Color(89, 198, 245), 1)
				: BorderFactory.createLineBorder(new Color(153, 140, 255), 1));
		JLabel brand = text("ARGONA", 22, TEXT, Font.BOLD);
		brand.setForeground(new Color(245, 247, 255));

		JPanel navGroup = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
		navGroup.setOpaque(false);
		navGroup.add(navButton("Home", true));
		navGroup.add(navButton("Feed", false));
		navGroup.add(navButton("Messages", false));
		navGroup.add(navButton("Profile", false));

		JLabel signedIn = text("Signed in as " + accountName, 12, MUTED, Font.PLAIN);
		signedIn.setAlignmentY(java.awt.Component.CENTER_ALIGNMENT);
		JButton logout = new JButton("Log out");
		logout.setForeground(TEXT);
		logout.setBackground(new Color(92, 77, 255));
		logout.setFocusPainted(false);
		logout.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(136, 124, 255), 1),
				BorderFactory.createEmptyBorder(10, 18, 10, 18)));
		logout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		logout.setAlignmentY(java.awt.Component.CENTER_ALIGNMENT);
		logout.setFont(new Font("SansSerif", Font.BOLD, 12));
		logout.setOpaque(true);
		logout.setContentAreaFilled(true);
		logout.setRolloverEnabled(true);
		logout.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseEntered(java.awt.event.MouseEvent e) { logout.setBackground(new Color(118, 102, 255)); }
			@Override
			public void mouseExited(java.awt.event.MouseEvent e) { logout.setBackground(new Color(92, 77, 255)); }
		});
		logout.addActionListener(e -> showLogin());

		brandGroup.add(logo);
		brandGroup.add(brand);
		brandGroup.setAlignmentY(java.awt.Component.CENTER_ALIGNMENT);
		topBar.add(brandGroup);
		topBar.add(Box.createHorizontalStrut(18));
		topBar.add(navGroup);
		topBar.add(Box.createHorizontalGlue());
		topBar.add(signedIn);
		topBar.add(Box.createHorizontalStrut(14));
		topBar.add(text(emailFor(accountName), 12, MUTED, Font.PLAIN));
		topBar.add(Box.createHorizontalStrut(10));
		topBar.add(actionButton("Language", e -> changeLanguage(accountName)));
		topBar.add(Box.createHorizontalStrut(8));
		topBar.add(actionButton("ARAI", e -> showArai(accountName)));
		topBar.add(Box.createHorizontalStrut(8));
		topBar.add(logout);

		javax.swing.JTabbedPane tabs = new javax.swing.JTabbedPane();
		appTabs = tabs;
		tabs.setBackground(PANEL);
		tabs.setForeground(TEXT);
		tabs.setOpaque(true);
		tabs.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
			@Override
			protected void installDefaults() {
				super.installDefaults();
				tabAreaInsets = new Insets(8, 0, 0, 0);
			}
		});
		tabs.addTab("Home", scrollable(homePanel(accountName)));
		tabs.addTab("Videos", scrollable(contentPanel("Watch", "Your video feed is ready for fresh ideas.")));
		tabs.addTab("Posts", scrollable(contentPanel("Discover", "Read the latest posts from your community.")));
		tabs.addTab("Chat", scrollable(contentPanel("Messages", "Your conversations will appear here.")));
		tabs.addTab("Stories", scrollable(contentPanel("Stories", "Share a moment with your friends.")));
		if (isAdminAccount(accountName)) {
			tabs.addTab("Admin", adminPanel(accountName));
		}
		tabs.addTab("Profile", scrollable(profilePanel(accountName)));

		shell.add(topBar, BorderLayout.NORTH);
		shell.add(tabs, BorderLayout.CENTER);
		add(shell, BorderLayout.CENTER);
		revalidate();
		repaint();
	}

	private JScrollPane scrollable(JPanel content) {
		JScrollPane scroll = new JScrollPane(content);
		scroll.setBorder(null);
		scroll.setBackground(BG);
		scroll.getViewport().setBackground(BG);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.getVerticalScrollBar().setUnitIncrement(16);
		return scroll;
	}

	private JPanel homePanel(String accountName) {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBackground(BG);
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		c.insets = new Insets(8, 18, 8, 18);

		JPanel hero = new JPanel(new BorderLayout(20, 0));
		hero.setBackground(cardColor(0));
		hero.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(themeAccent(), 1),
				BorderFactory.createEmptyBorder(24, 24, 24, 24)));
		hero.setPreferredSize(new Dimension(780, 175));

		JPanel heroText = new JPanel();
		heroText.setOpaque(false);
		heroText.setLayout(new BoxLayout(heroText, BoxLayout.Y_AXIS));
		JLabel live = text("LIVE", 11, new Color(4, 7, 11), Font.BOLD);
		live.setOpaque(true);
		live.setBackground(ACCENT_2);
		live.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		heroText.add(live);
		heroText.add(Box.createVerticalStrut(14));
		heroText.add(text(localizedGreeting() + ", " + accountName, 30, TEXT, Font.BOLD));
		heroText.add(Box.createVerticalStrut(10));
		heroText.add(text("Everything you care about, in one calm place.", 15, MUTED, Font.PLAIN));
		heroText.add(Box.createVerticalStrut(8));
		heroText.add(text("Country: " + countryName() + "  |  Language: " + languageName(), 12, ACCENT_2, Font.BOLD));

		JPanel heroMetrics = new JPanel(new GridLayout(2, 2, 12, 12));
		heroMetrics.setOpaque(false);
		heroMetrics.add(metricTile("Followers", "12.8K"));
		heroMetrics.add(metricTile("Stories", "148"));
		heroMetrics.add(metricTile("Posts", "2.4K"));
		heroMetrics.add(metricTile("Likes", "96%"));

		hero.add(heroText, BorderLayout.WEST);
		hero.add(heroMetrics, BorderLayout.EAST);

		c.gridy = 0;
		panel.add(hero, c);

		c.gridy++;
		panel.add(createBar(accountName), c);

		c.gridy++;
		panel.add(notificationPanel(), c);

		c.gridy++;
		panel.add(latestVersionCard(), c);

		c.gridy++;
		panel.add(sponsoredAdCard(), c);

		c.gridy++;
		JPanel stories = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
		stories.setOpaque(false);
		stories.add(storyBubble("A"));
		stories.add(storyBubble("L"));
		stories.add(storyBubble("K"));
		stories.add(storyBubble("N"));
		stories.add(storyBubble("S"));
		panel.add(stories, c);

		c.gridy++;
		JPanel metrics = new JPanel(new GridLayout(1, 3, 14, 0));
		metrics.setOpaque(false);
		metrics.add(metricCard("Engagement", "+24%"));
		metrics.add(metricCard("Views", "18.5K"));
		metrics.add(metricCard("Reach", "93%"));
		panel.add(metrics, c);

		c.gridy++;
		panel.add(featureCard("Your feed", "Posts, videos, stories and conversations from your people."), c);
		c.gridy++;
		panel.add(featureCard("Your space", "Update your profile and keep your Argona identity yours."), c);
		return panel;
	}

	private JPanel sponsoredAdCard() {
		JPanel card = new JPanel(new BorderLayout(14, 0));
		card.setBackground(new Color(20, 30, 42));
		card.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(70, 112, 142), 1),
				BorderFactory.createEmptyBorder(14, 16, 14, 16)));
		JPanel copy = new JPanel();
		copy.setOpaque(false);
		copy.setLayout(new BoxLayout(copy, BoxLayout.Y_AXIS));
		copy.add(text("SPONSORED", 10, ACCENT_2, Font.BOLD));
		copy.add(Box.createVerticalStrut(5));
		copy.add(text("Explore Microsoft products and services.", 15, TEXT, Font.BOLD));
		copy.add(Box.createVerticalStrut(4));
		copy.add(text("Opens the sponsor website in Microsoft Edge.", 12, MUTED, Font.PLAIN));
		card.add(copy, BorderLayout.CENTER);
		card.add(actionButton("Open ad", e -> openAdInEdge()), BorderLayout.EAST);
		return card;
	}

	private void openAdInEdge() {
		try {
			new ProcessBuilder("msedge.exe", SPONSORED_AD_URL).start();
		} catch (IOException exception) {
			try {
				if (Desktop.isDesktopSupported()) {
					Desktop.getDesktop().browse(new URI(SPONSORED_AD_URL));
				}
			} catch (Exception ignored) {
				javax.swing.JOptionPane.showMessageDialog(this, "Could not open the sponsored website.",
						"Advertisement", javax.swing.JOptionPane.WARNING_MESSAGE);
			}
		}
	}

	private JPanel latestVersionCard() {
		JPanel card = new JPanel(new BorderLayout(14, 0));
		card.setBackground(new Color(22, 29, 41));
		card.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(ACCENT, 1),
				BorderFactory.createEmptyBorder(14, 16, 14, 16)));
		JPanel copy = new JPanel();
		copy.setOpaque(false);
		copy.setLayout(new BoxLayout(copy, BoxLayout.Y_AXIS));
		copy.add(text("Argona latest version", 17, TEXT, Font.BOLD));
		copy.add(Box.createVerticalStrut(5));
		copy.add(text("Argona 29 (AR29) is ready to view.", 13, MUTED, Font.PLAIN));
		card.add(copy, BorderLayout.CENTER);
		card.add(actionButton("Open argona.upd", e -> showUpdateDialog()), BorderLayout.EAST);
		return card;
	}

	private void showUpdateDialog() {
		String updateText = "Argona latest version\n\nAR29\n\nClick AR29 to use Argona 29.";
		if (Files.exists(Paths.get("argona.upd"))) {
			try {
				updateText = new String(Files.readAllBytes(Paths.get("argona.upd")));
			} catch (IOException ignored) {
			}
		}
		JPanel updatePanel = new JPanel(new BorderLayout(12, 12));
		JTextArea details = new JTextArea(updateText);
		details.setEditable(false);
		details.setOpaque(false);
		details.setForeground(TEXT);
		details.setFont(new Font("SansSerif", Font.PLAIN, 14));
		updatePanel.add(details, BorderLayout.CENTER);
		JPanel platforms = new JPanel(new GridLayout(1, 5, 6, 0));
		platforms.setOpaque(false);
		String[] platformNames = {"Windows", "Linux", "macOS", "Android", "iOS"};
		for (String platform : platformNames) {
			platforms.add(actionButton("Download " + platform, e -> downloadPlatformUpdate(platform, details)));
		}
		updatePanel.add(platforms, BorderLayout.SOUTH);
		javax.swing.JOptionPane.showMessageDialog(this, updatePanel, "argona.upd", javax.swing.JOptionPane.PLAIN_MESSAGE);
	}

	private void downloadPlatformUpdate(String platform, JTextArea details) {
		if (ar29UpdateCount >= 2) {
			String error = "AR29 has already been updated 2 times.\nPlease close this update window and do not update again.";
			details.setText(error);
			javax.swing.JOptionPane.showMessageDialog(this, error, "AR29 update limit", javax.swing.JOptionPane.ERROR_MESSAGE);
			java.awt.Window updateWindow = javax.swing.SwingUtilities.getWindowAncestor(details);
			if (updateWindow != null) {
				updateWindow.dispose();
			}
			return;
		}
		String currentPlatform = currentOperatingSystem();
		if (!platform.equals(currentPlatform)) {
			String error = platform + " is not the operating system you are using.\n"
					+ "Your operating system is " + currentPlatform + ".\n\n"
					+ "Please select your operating system.";
			details.setText(error);
			javax.swing.JOptionPane.showMessageDialog(this, error, "Wrong operating system",
					javax.swing.JOptionPane.ERROR_MESSAGE);
			return;
		}
		String safePlatform = platform.replaceAll("[^A-Za-z0-9]", "");
		Path downloadFolder = Paths.get(System.getProperty("user.home"), "Downloads");
		Path updateFile = downloadFolder.resolve("Argona-29-" + safePlatform + ".upd");
		try {
			Files.createDirectories(downloadFolder);
			String update = "Argona 29 update\nPlatform: " + platform + "\nVersion: AR29\n";
			Files.write(updateFile, update.getBytes());
			ar29UpdateCount++;
			premiumBlueTheme = true;
			if (currentAccount != null) {
				openHome(currentAccount);
			}
			details.setText("Download complete.\n\nArgona 29 for " + platform + "\nSaved to:\n" + updateFile);
			javax.swing.JOptionPane.showMessageDialog(this, "Argona 29 for " + platform + " was downloaded.",
					"Download complete", javax.swing.JOptionPane.INFORMATION_MESSAGE);
		} catch (IOException exception) {
			details.setText("The download could not be saved for " + platform + ".");
		}
	}

	private String currentOperatingSystem() {
		String osName = System.getProperty("os.name", "").toLowerCase();
		if (osName.contains("win")) {
			return "Windows";
		}
		if (osName.contains("mac")) {
			return "macOS";
		}
		if (osName.contains("linux")) {
			return "Linux";
		}
		if (osName.contains("android")) {
			return "Android";
		}
		if (osName.contains("ios")) {
			return "iOS";
		}
		return System.getProperty("os.name", "Unknown OS");
	}

	private String localizedGreeting() {
		String language = selectedLanguage == null ? Locale.getDefault().getLanguage() : selectedLanguage;
		if ("es".equals(language)) {
			return "Buenas tardes";
		}
		if ("fr".equals(language)) {
			return "Bonsoir";
		}
		if ("de".equals(language)) {
			return "Guten Abend";
		}
		if ("it".equals(language)) {
			return "Buonasera";
		}
		if ("pt".equals(language)) {
			return "Boa tarde";
		}
		if ("ar".equals(language)) {
			return "Marhaba";
		}
		return "Good evening";
	}

	private String countryName() {
		String country = Locale.getDefault().getDisplayCountry(Locale.ENGLISH);
		return country.isEmpty() ? "Unknown" : country;
	}

	private String languageName() {
		String language = new Locale(selectedLanguage == null ? Locale.getDefault().getLanguage() : selectedLanguage)
				.getDisplayLanguage(Locale.ENGLISH);
		return language.isEmpty() ? "English" : language;
	}

	private void changeLanguage(String accountName) {
		String[] languages = {"English", "Spanish", "French", "German", "Italian", "Portuguese", "Arabic"};
		String[] codes = {"en", "es", "fr", "de", "it", "pt", "ar"};
		String choice = (String) javax.swing.JOptionPane.showInputDialog(this,
				"Choose your language:", "Language", javax.swing.JOptionPane.PLAIN_MESSAGE, null, languages,
					languageName());
		if (choice == null) {
			return;
		}
		for (int index = 0; index < languages.length; index++) {
			if (languages[index].equals(choice)) {
				selectedLanguage = codes[index];
				accounts.setProperty(LANGUAGE_PREFIX + accountName, selectedLanguage);
				saveAccounts();
				openHome(accountName);
				return;
			}
		}
	}

	private String emailFor(String accountName) {
		return "Email: " + accountName.toLowerCase(Locale.ENGLISH) + "@argona.app";
	}

	private void showArai(String accountName) {
		JTextArea chat = new JTextArea(12, 38);
		chat.setEditable(false);
		chat.setLineWrap(true);
		chat.setWrapStyleWord(true);
		chat.setBackground(INPUT);
		chat.setForeground(TEXT);
		chat.setText("ARAI: Hello " + accountName + "! I am your Argona assistant.\n"
				+ "Ask me about posts, videos, stories, updates, or your profile.\n\n");
		JTextField question = new JTextField();
		question.setBackground(INPUT);
		question.setForeground(TEXT);
		question.setCaretColor(TEXT);
		JButton ask = actionButton("Ask ARAI", e -> askArai(chat, question));
		question.addActionListener(e -> ask.doClick());
		JPanel input = new JPanel(new BorderLayout(8, 0));
		input.setOpaque(false);
		input.add(question, BorderLayout.CENTER);
		input.add(ask, BorderLayout.EAST);
		JPanel panel = new JPanel(new BorderLayout(10, 10));
		panel.setBackground(CARD);
		panel.add(new JScrollPane(chat), BorderLayout.CENTER);
		panel.add(input, BorderLayout.SOUTH);
		javax.swing.JOptionPane.showMessageDialog(this, panel, "ARAI - Argona AI", javax.swing.JOptionPane.PLAIN_MESSAGE);
	}

	private void askArai(JTextArea chat, JTextField question) {
		String prompt = question.getText().trim();
		if (prompt.isEmpty()) {
			return;
		}
		chat.append("You: " + prompt + "\nARAI: " + araiAnswer(prompt) + "\n\n");
		question.setText("");
		chat.setCaretPosition(chat.getDocument().getLength());
	}

	private String araiAnswer(String prompt) {
		String lowerPrompt = prompt.toLowerCase(Locale.ENGLISH);
		if (lowerPrompt.contains("post")) {
			return "Use Make post on Home to create a post. New posts appear in the Posts tab.";
		}
		if (lowerPrompt.contains("video")) {
			return "Use Add video on Home to choose a video. It will appear in Videos.";
		}
		if (lowerPrompt.contains("story")) {
			return "Use Add story on Home to share a story with your followers.";
		}
		if (lowerPrompt.contains("update") || lowerPrompt.contains("ar29")) {
			return "Open Argona latest version on Home to choose the correct AR29 update.";
		}
		if (lowerPrompt.contains("profile") || lowerPrompt.contains("account")) {
			return "Open Profile to change your profile image and view your account.";
		}
		return "I can help with posts, videos, stories, updates, profiles, and Argona navigation.";
	}

	private JPanel notificationPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBackground(CARD);
		panel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(BORDER, 1),
				BorderFactory.createEmptyBorder(14, 16, 14, 16)));
		panel.add(text("Notifications", 17, TEXT, Font.BOLD));
		panel.add(Box.createVerticalStrut(8));
		panel.add(notificationRow("Feeling tired? Come to Argona and watch videos.", "Open videos", "Videos"));
		panel.add(Box.createVerticalStrut(6));
		panel.add(notificationRow("Want to know something? Go to Argona and see posts.", "Open posts", "Posts"));
		panel.add(Box.createVerticalStrut(6));
		panel.add(notificationRow("Want to see your followers' stories? Come to Argona.", "Open stories", "Stories"));
		return panel;
	}

	private JPanel notificationRow(String message, String buttonText, String tabName) {
		JPanel row = new JPanel(new BorderLayout(12, 0));
		row.setOpaque(false);
		row.add(text(message, 13, MUTED, Font.PLAIN), BorderLayout.CENTER);
		row.add(actionButton(buttonText, e -> selectTab(tabName)), BorderLayout.EAST);
		return row;
	}

	private void selectTab(String tabName) {
		if (appTabs == null) {
			return;
		}
		for (int index = 0; index < appTabs.getTabCount(); index++) {
			if (tabName.equals(appTabs.getTitleAt(index))) {
				appTabs.setSelectedIndex(index);
				return;
			}
		}
	}

	private JPanel createBar(String accountName) {
		JPanel bar = new JPanel(new BorderLayout(14, 0));
		bar.setBackground(CARD);
		bar.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(BORDER, 1),
				BorderFactory.createEmptyBorder(12, 16, 12, 16)));
		bar.add(text("Share something new", 14, TEXT, Font.BOLD), BorderLayout.WEST);

		JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
		actions.setOpaque(false);
		actions.add(actionButton("Make post", e -> createPost(accountName)));
		actions.add(actionButton("Add video", e -> createVideo(accountName)));
		actions.add(actionButton("Add story", e -> createStory(accountName)));
		bar.add(actions, BorderLayout.CENTER);
		return bar;
	}

	private JButton actionButton(String label, java.awt.event.ActionListener action) {
		JButton button = new JButton(label);
		button.setForeground(TEXT);
		button.setBackground(new Color(38, 52, 70));
		button.setFocusPainted(false);
		button.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(74, 90, 110), 1),
				BorderFactory.createEmptyBorder(8, 12, 8, 12)));
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		button.addActionListener(action);
		return button;
	}

	private void createPost(String accountName) {
		JTextArea editor = new JTextArea(7, 34);
		editor.setLineWrap(true);
		editor.setWrapStyleWord(true);
		editor.setBackground(INPUT);
		editor.setForeground(TEXT);
		editor.setCaretColor(TEXT);
		editor.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
		int result = javax.swing.JOptionPane.showConfirmDialog(this, new JScrollPane(editor),
				"Create a post as @" + accountName, javax.swing.JOptionPane.OK_CANCEL_OPTION,
				javax.swing.JOptionPane.PLAIN_MESSAGE);
		if (result == javax.swing.JOptionPane.OK_OPTION && !editor.getText().trim().isEmpty()) {
			String postText = editor.getText().trim();
			if (autoBanToxicUser(accountName, postText)) {
				return;
			}
			createdPosts.add(postText);
			openHome(accountName);
			playNotificationSound();
			javax.swing.JOptionPane.showMessageDialog(this, "Your post was created.", "Post published",
					javax.swing.JOptionPane.INFORMATION_MESSAGE);
		}
	}

	private void createVideo(String accountName) {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Choose a video for @" + accountName);
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			createdVideos.add(chooser.getSelectedFile().getAbsolutePath());
			openHome(accountName);
			playNotificationSound();
			javax.swing.JOptionPane.showMessageDialog(this,
					"Video added: " + chooser.getSelectedFile().getName(), "Video uploaded",
					javax.swing.JOptionPane.INFORMATION_MESSAGE);
		}
	}

	private void createStory(String accountName) {
		String story = javax.swing.JOptionPane.showInputDialog(this,
				"What is happening?", "Create a story as @" + accountName,
				javax.swing.JOptionPane.PLAIN_MESSAGE);
		if (story != null && !story.trim().isEmpty()) {
			String storyText = story.trim();
			if (autoBanToxicUser(accountName, storyText)) {
				return;
			}
			createdStories.add(storyText);
			openHome(accountName);
			playNotificationSound();
			javax.swing.JOptionPane.showMessageDialog(this, "Your story was created.", "Story published",
					javax.swing.JOptionPane.INFORMATION_MESSAGE);
		}
	}

	private boolean autoBanToxicUser(String accountName, String content) {
		if (isAdminAccount(accountName)) {
			return false;
		}
		String lowerContent = content.toLowerCase();
		boolean targetsAdmin = lowerContent.contains("admin") || lowerContent.contains(DEFAULT_ADMIN.toLowerCase());
		String[] toxicTerms = {"idiot", "stupid", "loser", "trash", "shut up", "hate you", "dumb"};
		boolean containsToxicTerm = false;
		for (String toxicTerm : toxicTerms) {
			if (lowerContent.contains(toxicTerm)) {
				containsToxicTerm = true;
				break;
			}
		}
		if (!targetsAdmin || !containsToxicTerm) {
			return false;
		}
		accounts.setProperty(BANNED_PREFIX + accountName, "true");
		saveAccounts();
		javax.swing.JOptionPane.showMessageDialog(this,
				"Toxic language toward an admin is not allowed. This account has been banned.",
				"Account banned", javax.swing.JOptionPane.ERROR_MESSAGE);
		showLogin();
		return true;
	}

	private JPanel contentPanel(String title, String subtitle) {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBackground(BG);
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		c.insets = new Insets(12, 18, 12, 18);
		c.gridy = 0;
		JPanel sectionTitle = new JPanel(new BorderLayout());
		sectionTitle.setOpaque(false);
		sectionTitle.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
		sectionTitle.add(text(title, 24, TEXT, Font.BOLD), BorderLayout.WEST);
		panel.add(sectionTitle, c);
		c.gridy++;
		panel.add(featureCard(title, subtitle), c);
		c.gridy++;
		panel.add(featureCard("Featured", "A premium designer look with more depth and cleaner spacing."), c);
		List<String> createdItems = title.equals("Discover") ? createdPosts
				: title.equals("Watch") ? createdVideos : title.equals("Stories") ? createdStories : null;
		if (createdItems != null) {
			for (String item : createdItems) {
				c.gridy++;
				String display = title.equals("Watch") ? new File(item).getName() : item;
				panel.add(featureCard("Your " + title.toLowerCase(), display), c);
			}
		}
		return panel;
	}

	private JPanel profilePanel(String accountName) {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBackground(BG);
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		c.insets = new Insets(14, 18, 14, 18);

		JPanel profileCard = new JPanel(new BorderLayout(18, 0));
		profileCard.setBackground(CARD);
		profileCard.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(BORDER, 1),
				BorderFactory.createEmptyBorder(24, 24, 24, 24)));

		JLabel avatar = new JLabel("A", JLabel.CENTER);
		avatar.setOpaque(true);
		avatar.setBackground(Color.BLACK);
		avatar.setForeground(TEXT);
		avatar.setFont(new Font("SansSerif", Font.BOLD, 26));
		avatar.setPreferredSize(new Dimension(74, 74));
		avatar.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
		loadProfileImage(accountName, avatar);

		JPanel info = new JPanel();
		info.setOpaque(false);
		info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
		info.add(text("@" + accountName, 22, TEXT, Font.BOLD));
		info.add(Box.createVerticalStrut(8));
		info.add(text("Premium creator • Dark mode enabled", 13, MUTED, Font.PLAIN));
		info.add(Box.createVerticalStrut(8));
		info.add(text("Theme: Dark • Status: Online", 12, ACCENT_2, Font.BOLD));
		info.add(Box.createVerticalStrut(12));
		JButton chooseImage = new JButton("Choose profile image");
		chooseImage.setForeground(TEXT);
		chooseImage.setBackground(new Color(38, 52, 70));
		chooseImage.setFocusPainted(false);
		chooseImage.addActionListener(e -> chooseProfileImage(accountName, avatar));
		info.add(chooseImage);

		profileCard.add(avatar, BorderLayout.WEST);
		profileCard.add(info, BorderLayout.CENTER);

		c.gridy = 0;
		panel.add(profileCard, c);
		c.gridy++;
		panel.add(featureCard("Bio", "I build elegant experiences and keep the focus on the people who matter."), c);
		c.gridy++;
		panel.add(featureCard("Account", "Private account • 14 saved posts • 7 followers this week"), c);
		return panel;
	}

	private JPanel featureCard(String title, String subtitle) {
		JPanel card = new JPanel();
		card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
		card.setBackground(cardColor(1));
		card.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(BORDER, 1),
				BorderFactory.createEmptyBorder(24, 26, 24, 26)));
		card.setPreferredSize(new Dimension(780, 140));
		card.add(text(title, 20, TEXT, Font.BOLD));
		card.add(Box.createVerticalStrut(10));
		card.add(text(subtitle, 14, MUTED, Font.PLAIN));
		return card;
	}

	private JPanel metricTile(String label, String value) {
		JPanel tile = new JPanel();
		tile.setBackground(new Color(22, 29, 41));
		tile.setBorder(BorderFactory.createLineBorder(BORDER, 1));
		tile.setLayout(new BoxLayout(tile, BoxLayout.Y_AXIS));
		tile.setPreferredSize(new Dimension(120, 54));
		tile.add(text(value, 16, TEXT, Font.BOLD));
		tile.add(text(label, 10, MUTED, Font.PLAIN));
		return tile;
	}

	private JPanel metricCard(String label, String value) {
		JPanel card = new JPanel();
		card.setBackground(CARD);
		card.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(BORDER, 1),
				BorderFactory.createEmptyBorder(18, 18, 18, 18)));
		card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
		card.add(text(value, 20, TEXT, Font.BOLD));
		card.add(Box.createVerticalStrut(6));
		card.add(text(label, 12, MUTED, Font.PLAIN));
		return card;
	}

	private JPanel storyBubble(String letter) {
		JPanel bubble = new JPanel();
		bubble.setBackground(new Color(27, 35, 49));
		bubble.setBorder(BorderFactory.createLineBorder(ACCENT, 1));
		bubble.setPreferredSize(new Dimension(56, 56));
		bubble.setLayout(new GridBagLayout());
		JLabel l = text(letter, 20, TEXT, Font.BOLD);
		bubble.add(l);
		return bubble;
	}

	private Color cardColor(int index) {
		if (premiumBlueTheme) {
			return index % 2 == 0 ? new Color(8, 24, 42) : new Color(12, 36, 60);
		}
		return index % 2 == 0 ? CARD : CARD_ALT;
	}

	private Color themeAccent() {
		return premiumBlueTheme ? new Color(55, 174, 235) : ACCENT;
	}

	private JButton navButton(String label, boolean active) {
		JButton button = new JButton(label);
		button.setFocusPainted(false);
		button.setForeground(active ? TEXT : new Color(204, 214, 224));
		button.setBackground(premiumBlueTheme
				? (active ? new Color(20, 84, 126) : new Color(10, 38, 64))
				: (active ? new Color(36, 48, 67) : new Color(22, 29, 41)));
		button.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(active ? new Color(110, 92, 255) : new Color(58, 74, 96), 1),
				BorderFactory.createEmptyBorder(8, 12, 8, 12)));
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		button.setFont(new Font("SansSerif", Font.BOLD, 11));
		button.setOpaque(true);
		button.setContentAreaFilled(true);
		button.setRolloverEnabled(true);
		button.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseEntered(java.awt.event.MouseEvent e) {
				button.setBackground(new Color(42, 56, 77));
			}
			@Override
			public void mouseExited(java.awt.event.MouseEvent e) {
				button.setBackground(active ? new Color(36, 48, 67) : new Color(22, 29, 41));
			}
		});
		return button;
	}

	private void showLogin() {
		accounts.remove(LAST_ACCOUNT_KEY);
		saveAccounts();
		getContentPane().removeAll();
		setContentPane(buildPage());
		revalidate();
		repaint();
	}

	private static boolean isStrongPassword(String value) {
		return value.length() >= 8
				&& value.matches(".*[A-Z].*")
				&& value.matches(".*[a-z].*")
				&& value.matches(".*\\d.*")
				&& value.matches(".*[^A-Za-z0-9].*");
	}

	private void chooseProfileImage(String accountName, JLabel avatar) {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Choose profile image");
		if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
			return;
		}
		File imageFile = chooser.getSelectedFile();
		if (!imageFile.isFile()) {
			return;
		}
		accounts.setProperty(profileImageKey(accountName), imageFile.getAbsolutePath());
		saveAccounts();
		setAvatarImage(avatar, imageFile.getAbsolutePath());
	}

	private void loadProfileImage(String accountName, JLabel avatar) {
		String imagePath = accounts.getProperty(profileImageKey(accountName));
		if (imagePath != null) {
			setAvatarImage(avatar, imagePath);
		}
	}

	private static void setAvatarImage(JLabel avatar, String imagePath) {
		ImageIcon source = new ImageIcon(imagePath);
		if (source.getIconWidth() <= 0 || source.getIconHeight() <= 0) {
			return;
		}
		Image scaled = source.getImage().getScaledInstance(74, 74, Image.SCALE_SMOOTH);
		avatar.setText("");
		avatar.setIcon(new ImageIcon(scaled));
	}

	private static String profileImageKey(String accountName) {
		return "profileImage." + accountName;
	}

	private static Properties loadAccounts() {
		Properties loaded = new Properties();
		if (Files.exists(ACCOUNTS_FILE)) {
			try (InputStream input = Files.newInputStream(ACCOUNTS_FILE)) {
				loaded.load(input);
			} catch (IOException ignored) {
			}
		}
		if (!loaded.containsKey(DEFAULT_ADMIN)) {
			loaded.setProperty(DEFAULT_ADMIN, "ar1234");
		}
		if (!loaded.containsKey(ROLE_PREFIX + DEFAULT_ADMIN)) {
			loaded.setProperty(ROLE_PREFIX + DEFAULT_ADMIN, "admin");
		}
		return loaded;
	}

	private void saveAccounts() {
		try (OutputStream output = Files.newOutputStream(ACCOUNTS_FILE)) {
			accounts.store(output, "Argona accounts");
		} catch (IOException exception) {
			showMessage("Could not save account.", ERROR);
		}
	}

	private void showMessage(String value, Color color) {
		message.setText(value);
		message.setForeground(color);
	}

	private static JPanel verticalPanel() {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		return panel;
	}

	private static JLabel text(String value, int size, Color color, int style) {
		JLabel label = new JLabel(value);
		label.setFont(new Font("SansSerif", style, size));
		label.setForeground(color);
		return label;
	}

	private static void showLoadingScreen() {
		JWindow loading = new JWindow();
		JPanel content = new JPanel(new GridBagLayout());
		content.setBackground(Color.BLACK);

		JPanel logoStack = new JPanel();
		logoStack.setOpaque(false);
		logoStack.setLayout(new BoxLayout(logoStack, BoxLayout.Y_AXIS));

		LoadingLogo logo = new LoadingLogo();
		logo.setPreferredSize(new Dimension(76, 76));
		logo.setMaximumSize(new Dimension(76, 76));
		logo.setAlignmentX(0.5f);

		JLabel title = text("ARGONA", 28, Color.WHITE, Font.BOLD);
		title.setAlignmentX(0.5f);
		JLabel status = text("Load the memory of Argona (.)", 13, new Color(170, 170, 170), Font.PLAIN);
		status.setAlignmentX(0.5f);
		JProgressBar progress = new JProgressBar(0, 100);
		progress.setValue(0);
		progress.setStringPainted(true);
		progress.setString("0%");
		progress.setForeground(Color.WHITE);
		progress.setBackground(new Color(35, 35, 35));
		progress.setBorder(BorderFactory.createLineBorder(new Color(90, 90, 90), 1));
		progress.setPreferredSize(new Dimension(210, 18));
		progress.setMaximumSize(new Dimension(210, 18));
		progress.setAlignmentX(0.5f);

		logoStack.add(logo);
		logoStack.add(Box.createVerticalStrut(18));
		logoStack.add(title);
		logoStack.add(Box.createVerticalStrut(8));
		logoStack.add(status);
		logoStack.add(Box.createVerticalStrut(16));
		logoStack.add(progress);
		content.add(logoStack);

		loading.setContentPane(content);
		loading.setSize(360, 260);
		loading.setLocationRelativeTo(null);
		loading.setVisible(true);
		final int[] animationStep = {0};
		javax.swing.Timer animation = new javax.swing.Timer(100, event -> {
			animationStep[0] = (animationStep[0] + 1) % 100;
			int percent = Math.min(99, animationStep[0] + 1);
			String dots = animationStep[0] % 4 == 0 ? "(.)" : animationStep[0] % 4 == 1 ? "(..)" : animationStep[0] % 4 == 2 ? "(...)" : "(....)";
			progress.setValue(percent);
			progress.setString(percent + "%");
			status.setText("Load the memory of Argona " + dots);
			logo.animate(animationStep[0]);
		});
		animation.start();
		new javax.swing.Timer(10000, event -> {
			((javax.swing.Timer) event.getSource()).stop();
			animation.stop();
			progress.setValue(100);
			progress.setString("100%");
			playLoadingSound(loading);
			loading.dispose();
			argona app = new argona();
			app.setVisible(true);
			app.showWelcomeNotification();
		}).start();
	}

	private static void playLoadingSound(JWindow loading) {
		Path soundFile = Paths.get(System.getProperty("user.dir"), "loading-complete.mp3");
		try {
			if (Files.exists(soundFile)) {
				String escapedPath = soundFile.toAbsolutePath().toString().replace("'", "''");
				String command = "$player = New-Object -ComObject WMPlayer.OCX; "
						+ "$player.settings.volume = 100; "
						+ "$player.URL = '" + escapedPath + "'; "
						+ "$player.controls.play();";
				new ProcessBuilder("powershell.exe", "-NoProfile", "-WindowStyle", "Hidden", "-Command", command).start();
			} else {
				Toolkit.getDefaultToolkit().beep();
			}
		} catch (IOException | SecurityException exception) {
			Toolkit.getDefaultToolkit().beep();
		}
	}

	private void showWelcomeNotification() {
		String title = "Argona is ready";
		String message = "Come to Argona to watch videos, read posts, and see stories.";
		if (java.awt.SystemTray.isSupported()) {
			try {
				java.awt.TrayIcon trayIcon = new java.awt.TrayIcon(createAppIcon(), "Argona");
				trayIcon.setImageAutoSize(true);
				java.awt.SystemTray tray = java.awt.SystemTray.getSystemTray();
				tray.add(trayIcon);
				trayIcon.displayMessage(title, message, java.awt.TrayIcon.MessageType.INFO);
				new javax.swing.Timer(7000, event -> {
					((javax.swing.Timer) event.getSource()).stop();
					tray.remove(trayIcon);
				}).start();
			} catch (java.awt.AWTException ignored) {
				showWelcomeDialog(title, message);
			}
		} else {
			showWelcomeDialog(title, message);
		}
	}

	private void showWelcomeDialog(String title, String message) {
		new javax.swing.Timer(500, event -> {
			((javax.swing.Timer) event.getSource()).stop();
			javax.swing.JOptionPane.showMessageDialog(this, message, title, javax.swing.JOptionPane.INFORMATION_MESSAGE);
		}).start();
	}

	private void showExitNotification() {
		String title = "Thanks for choosing Argona";
		String message = "Have a good day! See you next time.";
		if (java.awt.SystemTray.isSupported()) {
			try {
				java.awt.TrayIcon trayIcon = new java.awt.TrayIcon(createAppIcon(), "Argona");
				trayIcon.setImageAutoSize(true);
				java.awt.SystemTray tray = java.awt.SystemTray.getSystemTray();
				tray.add(trayIcon);
				trayIcon.displayMessage(title, message, java.awt.TrayIcon.MessageType.INFO);
				new javax.swing.Timer(1800, event -> {
					((javax.swing.Timer) event.getSource()).stop();
					tray.remove(trayIcon);
					dispose();
					System.exit(0);
				}).start();
				return;
			} catch (java.awt.AWTException ignored) {
			}
		}
		javax.swing.JOptionPane.showMessageDialog(this, message, title, javax.swing.JOptionPane.INFORMATION_MESSAGE);
		dispose();
		System.exit(0);
	}

	private static void playNotificationSound() {
		Path soundFile = Paths.get(System.getProperty("user.dir"), "notification.mp3");
		Thread soundThread = new Thread(() -> {
			try {
				if (Files.exists(soundFile)) {
					String escapedPath = soundFile.toAbsolutePath().toString().replace("'", "''");
					String command = "$player = New-Object -ComObject WMPlayer.OCX; "
							+ "$player.settings.volume = 100; "
							+ "$player.URL = '" + escapedPath + "'; "
							+ "$player.controls.play();";
					new ProcessBuilder("powershell.exe", "-NoProfile", "-WindowStyle", "Hidden", "-Command", command).start();
				}
			} catch (IOException | SecurityException ignored) {
			}
		}, "argona-notification-sound");
		soundThread.setDaemon(true);
		soundThread.start();
	}

	private static class LoadingLogo extends JPanel {
		private double rotation;
		private double pulse;

		private LoadingLogo() {
			setOpaque(false);
		}

		private void animate(int step) {
			rotation = step * Math.PI / 16.0;
			pulse = 0.5 + 0.5 * Math.sin(step * Math.PI / 10.0);
			repaint();
		}

		@Override
		protected void paintComponent(Graphics rawGraphics) {
			super.paintComponent(rawGraphics);
			Graphics2D graphics = (Graphics2D) rawGraphics.create();
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			int centerX = getWidth() / 2;
			int centerY = getHeight() / 2;
			int radius = 28;
			int glow = (int) (35 + pulse * 35);
			graphics.setColor(new Color(255, 255, 255, glow));
			graphics.fillOval(centerX - radius - 5, centerY - radius - 5, (radius + 5) * 2, (radius + 5) * 2);
			graphics.setColor(Color.BLACK);
			graphics.fillRoundRect(centerX - radius, centerY - radius, radius * 2, radius * 2, 14, 14);
			graphics.setColor(new Color(145, 145, 145));
			graphics.drawArc(centerX - radius - 6, centerY - radius - 6, (radius + 6) * 2, (radius + 6) * 2,
					(int) Math.toDegrees(rotation), 235);
			graphics.setColor(Color.WHITE);
			graphics.drawArc(centerX - radius - 6, centerY - radius - 6, (radius + 6) * 2, (radius + 6) * 2,
					(int) Math.toDegrees(rotation) + 235, 55);
			graphics.setColor(new Color(235, 235, 235));
			graphics.setFont(new Font("SansSerif", Font.BOLD, 36));
			String letter = "A";
			int textWidth = graphics.getFontMetrics().stringWidth(letter);
			int baseline = centerY + graphics.getFontMetrics().getAscent() / 2 - 2;
			graphics.drawString(letter, centerX - textWidth / 2, baseline);
			graphics.dispose();
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(argona::showLoadingScreen);
	}
}