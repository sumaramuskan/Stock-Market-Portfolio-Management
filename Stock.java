import java.io.*;
import java.sql.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Stock {
    static Connection con;
    static Scanner sc = new Scanner(System.in);
    static int id;
     static Map<Integer, ShareDetails> userShares = new HashMap<>();
    static ArrayList<ShareDetails> user_shares;

    public static void main(String[] args) throws Exception {
        String dburl = "jdbc:mysql://localhost:3306/stockmarket";
        String dbuser = "root";
        String dbpass = "";
        String Driver = "com.mysql.cj.jdbc.Driver";
        con = DriverManager.getConnection(dburl, dbuser, dbpass);
        Statement st = con.createStatement();

        if (con != null) {
            // System.out.println("Connection SuccessFull");
        } else {
            System.out.println("Connection Failed");
        }
        while (true) {
            System.out.println("------------------------------");
            System.out.printf("%-2s | %-15s%n", "Option", "Description");
            System.out.println("------------------------------");
            System.out.printf("%-6s | %-15s%n", "1", "New Registration");
            System.out.printf("%-6s | %-15s%n", "2", "Login");
            System.out.printf("%-6s | %-15s%n", "3", "Exit");
            System.out.println("------------------------------");
            int choice = sc.nextInt();
            switch (choice) {
                case 1:
                    registerUser();
                    break;
                case 2:

                    System.out.println("=== User Login ===");
                    System.out.print("Enter Username: ");
                    sc.nextLine();
                    String username = sc.nextLine();

                    System.out.print("Enter Your Password: ");
                    String password = sc.nextLine();

                    if (authenticateUser(username, password)) {
                        String sql = "SELECT user_id FROM user WHERE username=? AND password=?";
                        PreparedStatement pst = con.prepareStatement(sql);
                        pst.setString(1, username); // Set the username as the first parameter
                        pst.setString(2, password); // Set the password as the second parameter
                        ResultSet rs = pst.executeQuery();

                        if (rs.next()) {
                            id = rs.getInt(1);
                            System.out.println("Login Successful!");

                            System.out.print("Do you want to update your account balance? (Enter 'Yes' to Update): ");
                            String response = sc.next();

                            if (response.equalsIgnoreCase("yes")) {
                                updateAccountBalance(id);
                            }

                            operation();
                            break;
                        }
                    } else {
                        System.out.println("Login Failed. Please check your username and password.");
                        break;

                    }

                case 3:
                    System.exit(0);

                default:
                    System.out.println("Please Enter Valid Choice");

            }
        }

    }

    public static void updateAccountBalance(int userId) {
        try {
            // Create a Scanner object to get user input
            Scanner sc = new Scanner(System.in);

            // Prompt the user for the new balance
            System.out.println("Enter the new balance: ");
            double newBalance = sc.nextDouble();

            // Close the Scanner

            // Update the account balance in the database
            String updateSql = "UPDATE user SET amount=? WHERE user_id=?";
            PreparedStatement updatePst = con.prepareStatement(updateSql);
            updatePst.setDouble(1, newBalance); // Set the new balance
            updatePst.setInt(2, userId); // Set the user ID
            int rowsUpdated = updatePst.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Account balance updated successfully.");
            } else {
                System.out.println("Failed to update account balance.");
            }
        } catch (SQLException e) {
            // e.printStackTrace();
        }
    }

    public static void operation() throws SQLException, IOException {
        while (true) {
            System.out.println("----------------------------");
            System.out.printf("%-2s | %-15s%n", "Option", "Description");
            System.out.println("----------------------------");
            System.out.printf("%-6s | %-15s%n", "1", "Buy");
            System.out.printf("%-6s | %-15s%n", "2", "Sell");
            System.out.printf("%-6s | %-15s%n", "3", "View Portfolio");
            System.out.printf("%-6s | %-15s%n", "4", "View Transaction History");

            System.out.printf("%-6s | %-15s%n", "5", "Back");
            System.out.println("----------------------------");
            int choice = sc.nextInt();
            switch (choice) {
                case 1:
                    buyShare();
                    break;
                case 2:
                    SellShare();
                    break;

                case 3:
                    View();
                    break;
                case 4:
                    viewTransaction(id);
                    break;

                case 5:
                    return;

                default:
                    System.out.println("Please Enter Valid Choice");
                    break;

            }
        }

    }

    public static void buyShare() {
        try {
            // Display available shares with PE_ratio and EPS
            String sql = "SELECT id, symbol, company_name, price, PE_ratio, EPS , industry , dividend FROM stockmarket";
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            System.out.println("Available Shares:");
            System.out.println(
                    "-------------------------------------------------------------------------------------------------------------------------------");
            System.out.printf("%-6s | %-9s | %-25s | %-30s | %-10s | %-10s | %-10s | %-10s%n",
                    "ID", "Symbol", "Company Name", "Price", "PE Ratio", "EPS", "Industry", "Dividend");
            System.out.println(
                    "-------------------------------------------------------------------------------------------------------------------------------");
            while (rs.next()) {
                System.out.printf("%-6d | %-9s | %-25s | %-30.2f | %-10.2f | %-10.2f | %-10s | %-10.2f%n",
                        rs.getInt("id"),
                        rs.getString("symbol"),
                        rs.getString("company_name"),
                        rs.getDouble("price"),
                        rs.getDouble("PE_ratio"),
                        rs.getDouble("EPS"),
                        rs.getString("industry"),
                        rs.getDouble("dividend"));
            }

            // Get user input for share purchase
            System.out.print("Enter the ID of the share you want to buy: ");
            int choice = sc.nextInt();

            // Check if the selected choice is valid
            if (!isValidShareId(choice)) {
                System.out.println("Invalid share ID.");
                return;
            }

            // Get the share details based on the chosen ID
            ShareDetails shareDetails = getShareDetailsById(choice);

            // Get user input for the number of shares to buy
            System.out.print("Enter the number of shares you want to buy: ");
            int quantity = sc.nextInt();

            // Calculate the total cost
            double totalCost = quantity * shareDetails.getShare_price();

            // Check if the user has sufficient balance
            double userBalance = getUserBalance(id);
            if (totalCost > userBalance) {
                System.out.println("Insufficient balance to buy shares.");
                return;
            }

            // Start a database transaction
            con.setAutoCommit(false);

            try {
                // Insert a new record into the user_portfolio table
                insertIntoUserPortfolio(id, shareDetails.getSymbol(), shareDetails.getShare_name(),
                        shareDetails.getShare_price(), quantity, totalCost);

                // Update the user's balance
                updateBalance(id, -totalCost);

                // Update the share price in the database
                updateSharePricesInDatabase();

                // Initialize the user_shares ArrayList if not already initialized
                if (user_shares == null) {
                    user_shares = new ArrayList<>();
                }

                // Add the bought shares to the user_shares ArrayList
                user_shares.add(
                        new ShareDetails(quantity, totalCost, shareDetails.getShare_name(),
                                shareDetails.getShare_price(),
                                shareDetails.getSymbol(), shareDetails.getAmount(), id));
                updatePortfolio(choice, shareDetails, quantity, true);

                // Commit the transaction
                con.commit();

                // Save transaction details
                try {
                    saveTransactionDetails(id, shareDetails.getSymbol(), "BUY", quantity, shareDetails.getShare_price(),
                            totalCost);
                } catch (IOException e) {
                    // e.printStackTrace();
                }

                System.out.println("Purchase successful!");
            } catch (SQLException e) {
                // Roll back the transaction in case of an error
                con.rollback();
                e.printStackTrace();
                System.out.println("Error while buying shares.");
            } finally {
                // Reset auto-commit mode
                con.setAutoCommit(true);
            }
        } catch (SQLException e) {
            // e.printStackTrace();
            System.out.println("Error while accessing the database.");
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid number.");
        }
    }

    private static void insertIntoUserPortfolio(int userId, String symbol, String shareName, double sharePrice,
            int quantity, double investment) {
        String insertSQL = "INSERT INTO user_portfolio (user_id, symbol, quantity, investment) VALUES (?, ?, ?, ?)";

        try {
            PreparedStatement insertPst = con.prepareStatement(insertSQL);
            insertPst.setInt(1, userId);
            insertPst.setString(2, symbol);
            // insertPst.setString(3, shareName);
            // insertPst.setDouble(4, sharePrice);
            insertPst.setInt(3, quantity);
            insertPst.setDouble(4, investment);
            insertPst.executeUpdate();
        } catch (SQLException e) {
            // e.printStackTrace();
            // System.out.println("Error while inserting into user_portfolio");
        }
    }

    // Check if the share ID is valid
    private static boolean isValidShareId(int shareId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM stockmarket WHERE id = ?";
        PreparedStatement pst = con.prepareStatement(sql);
        pst.setInt(1, shareId);
        ResultSet rs = pst.executeQuery();
        rs.next();
        return rs.getInt(1) > 0;
    }

    // Get share details by ID
    private static ShareDetails getShareDetailsById(int shareId) throws SQLException {
        String sql = "SELECT * FROM stockmarket WHERE id = ?";
        PreparedStatement pst = con.prepareStatement(sql);
        pst.setInt(1, shareId);
        ResultSet rs = pst.executeQuery();
        rs.next();
        return new ShareDetails(
                0, // Quantity (will be set when buying)
                0.0, // Investment (will be set when buying)
                rs.getString("company_name"),
                rs.getDouble("price"),
                rs.getString("symbol"),
                0, // Amount (not used here)
                0 // User ID (not used here)
        );
    }

    // Get the user's balance
    private static double getUserBalance(int userId) throws SQLException {
        String sql = "SELECT amount FROM user WHERE user_id = ?";
        PreparedStatement pst = con.prepareStatement(sql);
        pst.setInt(1, userId);
        ResultSet rs = pst.executeQuery();
        rs.next();
        return rs.getDouble("amount");
    }

    // Update the user's balance
    private static void updateBalance(int userId, double amount) throws SQLException {
        String sql = "UPDATE user SET amount = amount + ? WHERE user_id = ?";
        PreparedStatement pst = con.prepareStatement(sql);
        pst.setDouble(1, amount);
        pst.setInt(2, userId);
        pst.executeUpdate();
    }

    public static void updateSharePricesInDatabase() {
        try {
            // Retrieve all shares from the stockmarket table
            String selectSql = "SELECT id, price FROM stockmarket";
            PreparedStatement selectPst = con.prepareStatement(selectSql);
            ResultSet rs = selectPst.executeQuery();

            // Update the share prices for each share
            while (rs.next()) {
                int shareId = rs.getInt("id");
                double currentPrice = rs.getDouble("price");

                // Generate a random value to determine whether to increase or decrease the
                // price
                Random rand = new Random();
                int changeDirection = rand.nextBoolean() ? 1 : -1; // 1 for increase, -1 for decrease
                double priceChangePercentage = rand.nextDouble() * 0.1; // Adjust the range as needed

                // Calculate the new share price
                double newSharePrice = currentPrice * (1 + (priceChangePercentage * changeDirection));

                // Update the share price in the database
                String updateSql = "UPDATE stockmarket SET price = ? WHERE id = ?";
                PreparedStatement updatePst = con.prepareStatement(updateSql);
                updatePst.setDouble(1, newSharePrice);
                updatePst.setInt(2, shareId);
                updatePst.executeUpdate();
            }

            // System.out.println("Share prices updated in the database.");
        } catch (SQLException e) {
            // e.printStackTrace();
            // System.out.println("Error while updating share prices in the database.");
        }
    }

    private static void updatePortfolio(int userId, ShareDetails shareDetails, int quantity, boolean isBuy)
            throws SQLException {
        try {
            // Check if the user already has this share in their portfolio
            String checkPortfolioSQL = "SELECT * FROM user_portfolio WHERE user_id = ? AND symbol = ?";
            PreparedStatement checkPortfolioPst = con.prepareStatement(checkPortfolioSQL);
            checkPortfolioPst.setInt(1, userId);
            checkPortfolioPst.setString(2, shareDetails.getSymbol());
            ResultSet portfolioRs = checkPortfolioPst.executeQuery();

            if (portfolioRs.next()) {
                // If the user already has this share, update the quantity and investment
                int currentQuantity = portfolioRs.getInt("quantity");
                double currentInvestment = portfolioRs.getDouble("investment");

                if (isBuy) {
                    int newQuantity = currentQuantity + quantity;
                    double newInvestment = currentInvestment + (quantity * shareDetails.getShare_price());
                    updatePortfolioRecord(userId, shareDetails.getSymbol(), newQuantity, newInvestment);
                } else {
                    // Check if the user has enough shares to sell
                    if (currentQuantity >= quantity) {
                        int newQuantity = currentQuantity - quantity;
                        double newInvestment = currentInvestment - (quantity * shareDetails.getShare_price());
                        updatePortfolioRecord(userId, shareDetails.getSymbol(), newQuantity, newInvestment);
                    } else {
                        System.out.println("Insufficient shares to sell.");
                    }
                }
            } else {
                // If the user doesn't have this share, handle based on buy or sell
                if (isBuy) {
                    insertPortfolioRecord(userId, shareDetails.getSymbol(), quantity,
                            quantity * shareDetails.getShare_price());
                } else {
                    System.out.println("User does not have shares to sell.");
                }
            }
        } catch (SQLException e) {
            // e.printStackTrace();
            // System.out.println("Error while updating user portfolio.");
        }
    }

    private static void updatePortfolioRecord(int userId, String symbol, int quantity, double investment)
            throws SQLException {
        String updatePortfolioSQL = "UPDATE user_portfolio SET quantity = ?, investment = ? WHERE user_id = ? AND symbol = ?";
        PreparedStatement updatePortfolioPst = con.prepareStatement(updatePortfolioSQL);
        updatePortfolioPst.setInt(1, quantity);
        updatePortfolioPst.setDouble(2, investment);
        updatePortfolioPst.setInt(3, userId);
        updatePortfolioPst.setString(4, symbol);
        updatePortfolioPst.executeUpdate();
    }

    private static void insertPortfolioRecord(int userId, String symbol, int quantity, double investment)
            throws SQLException {
        String insertPortfolioSQL = "INSERT INTO user_portfolio (user_id, symbol, quantity, investment) VALUES (?, ?, ?, ?)";
        PreparedStatement insertPortfolioPst = con.prepareStatement(insertPortfolioSQL);
        insertPortfolioPst.setInt(1, userId);
        insertPortfolioPst.setString(2, symbol);
        insertPortfolioPst.setInt(3, quantity);
        insertPortfolioPst.setDouble(4, investment);
        insertPortfolioPst.executeUpdate();
    }

    public static void SellShare() throws SQLException, IOException {
        // Check if the user has any shares to sell in the database
        List<ShareDetails> userPortfolio = getUserPortfolioFromDB();

        if (userPortfolio != null && !userPortfolio.isEmpty()) {
            // Display the shares owned by the user

            System.out.println("Shares Owned:");
            // System.out.printf("%-12s | %-15s | %-12s | %-16s | %-15s | %-20s%n",
            // "Symbol", "Company Name", "Share Price", "Quantity Owned", "User's
            // Investment",
            // "Latest Share Price");
            System.out.println(
                    "--------------------------------------------------------------------------------------------");
            for (ShareDetails share : userPortfolio) {
                double latestSharePrice = getLatestSharePrice(share.getSymbol());
                // System.out.printf("%-12s | %-15s | %-12.2f | %-16d | %-15d | %-20.2f%n",
                // share.getSymbol(), share.getShare_name(), share.getShare_price(),
                // share.getQuantity(), share.getInvestment(), latestSharePrice);
                // double latestSharePrice = getLatestSharePrice(share.getSymbol());
                System.out.println("Symbol: " + share.getSymbol());
                System.out.println("Company Name: " + share.getShare_name());
                System.out.println("Purchase Price: " + share.getShare_price()); // Display purchase price
                System.out.println("Latest Share Price: " + latestSharePrice); // Display latest share price
                System.out.println("Quantity Owned: " + share.getQuantity());
                System.out.println("User's Investment: " + share.getInvestment());
                System.out.println("------------------------");

            }
            System.out.println(
                    "--------------------------------------------------------------------------------------------");
        }

        // Input the symbol of the share the user wants to sell
        System.out.println("Enter the symbol of the share you want to sell:");
        String symbolToSell = sc.next();

        // Find the share with the matching symbol in the user's portfolio
        ShareDetails shareToSell = null;
        for (ShareDetails share : userPortfolio) {
            if (share.getSymbol().equalsIgnoreCase(symbolToSell)) {
                shareToSell = share;
                break;
            }
        }

        if (shareToSell == null) {
            System.out.println("You don't own shares with symbol " + symbolToSell);
        } else {
            // Retrieve the latest share price from the stock market table
            double latestSharePrice = getLatestSharePrice(symbolToSell);

            // Input the quantity to sell
            System.out.println("Enter the quantity of shares you want to sell:");
            int sellQuantity = sc.nextInt();

            if (sellQuantity <= 0 || sellQuantity > shareToSell.getQuantity()) {
                System.out.println("Invalid quantity to sell");
            } else {
                double totalSellAmount = sellQuantity * latestSharePrice;

                // Calculate the profit or loss amount
                double profitOrLoss = (latestSharePrice - shareToSell.getShare_price()) * sellQuantity;

                // Calculate the profit or loss percentage
                double profitOrLossPercentage = (profitOrLoss / (shareToSell.getShare_price() * sellQuantity)) * 100;

                // Update the user's balance in usertable
                updateBalance(id, totalSellAmount);

                // Update the user's portfolio in the database in user_portfolio table
                updatePortfolio(id, shareToSell, sellQuantity, false);

                // Save the transaction details to a file
                saveTransactionDetails(id, symbolToSell, "SELL", sellQuantity, latestSharePrice, totalSellAmount);

                System.out.println("=== Sell Successful ===");
                System.out.println("Profit/Loss Amount: $" + profitOrLoss);
                System.out.println("Profit/Loss Percentage: " + profitOrLossPercentage + "%");

                // Update the user's portfolio locally
                shareToSell.setQuantity(shareToSell.getQuantity() - sellQuantity);
                // Check if the quantity of shares is now 0
                if (shareToSell.getQuantity() == 0) {
                    // Delete the row from the user's portfolio table in the database
                    deleteShareFromUserPortfolio(id, shareToSell.getSymbol());
                }

            }
        }
        updateSharePricesInDatabase();
    }

    public static void deleteShareFromUserPortfolio(int userId, String symbol) throws SQLException {
        // Define the SQL DELETE statement
        String deleteSQL = "DELETE FROM user_portfolio WHERE user_id = ? AND symbol = ?";

        try (PreparedStatement pstmt = con.prepareStatement(deleteSQL)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, symbol);

            // Execute the DELETE statement
            int deletedRows = pstmt.executeUpdate();

            if (deletedRows > 0) {
                System.out.println("Share with symbol " + symbol + " deleted from user's portfolio.");
            } else {
                System.out.println("Share with symbol " + symbol + " not found in user's portfolio.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to delete share from user's portfolio.");
        }
    }

    private static double getLatestSharePrice(String symbol) throws SQLException {
        // Retrieve the latest share price from the stock market table based on the
        // symbol
        String sql = "SELECT price FROM stockmarket WHERE symbol = ?";
        PreparedStatement pst = con.prepareStatement(sql);
        pst.setString(1, symbol);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            return rs.getDouble("price");
        } else {
            // Handle the case where the share symbol is not found in the stock market table
            System.out.println("Share symbol not found in the stock market table.");
            return 0.0; // Return a default value or handle as needed
        }
    }

    private static List<ShareDetails> getUserPortfolioFromDB() {
        List<ShareDetails> userPortfolio = new ArrayList<>();

        String userPortfolioSQL = "SELECT up.symbol, sm.company_name, sm.price, up.quantity, up.investment " +
                "FROM user_portfolio up " +
                "JOIN stockmarket sm ON up.symbol = sm.symbol " +
                "WHERE up.user_id = ?";

        try {
            PreparedStatement userPortfolioPst = con.prepareStatement(userPortfolioSQL);
            userPortfolioPst.setInt(1, id);
            ResultSet userPortfolioRs = userPortfolioPst.executeQuery();

            if (userPortfolioRs.next()) {
                System.out.println("User Portfolio:");
                System.out.printf("%-15s %-25s %-15s %-15s %-20s%n",
                        "Symbol", "Company Name", "Share Price", "Quantity Owned", "User's Investment");
                System.out.println("---------------------------------------------------------------");

                do {
                    String symbol = userPortfolioRs.getString("symbol");
                    String shareName = userPortfolioRs.getString("company_name");
                    double sharePrice = userPortfolioRs.getDouble("price");
                    int quantity = userPortfolioRs.getInt("quantity");
                    double investment = userPortfolioRs.getDouble("investment");
                    String getUserAmountSQL = "SELECT amount FROM user WHERE user_id = ?";
                    long userAmount = (long) 0.0; // Default value, change as needed

                    try {
                        PreparedStatement getUserAmountPst = con.prepareStatement(getUserAmountSQL);
                        getUserAmountPst.setInt(1, id);
                        ResultSet userAmountRs = getUserAmountPst.executeQuery();

                        if (userAmountRs.next()) {
                            userAmount = userAmountRs.getLong("amount");
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        System.out.println("Failed to fetch user amount");
                    }

                    System.out.printf("%-15s %-25s %-15s %-15s %-20s%n",
                            symbol, shareName, sharePrice, quantity, investment);

                    // Create a ShareDetails object and add it to the user's portfolio
                    ShareDetails share = new ShareDetails(quantity, investment, shareName, sharePrice, symbol,
                            userAmount, id);

                    // ShareDetails share = new ShareDetails(symbol, shareName, sharePrice,
                    // quantity, investment);
                    userPortfolio.add(share);
                } while (userPortfolioRs.next());
            } else {
                System.out.println("You don't own any shares.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to fetch user portfolio");
        }

        return userPortfolio;
    }

   
    public static void saveTransactionDetails(int userId, String symbol, String transactionType, int quantity,
            double sharePrice, double transactionAmount) throws IOException {
        try {
            String fileName = "user" + userId + "_transactions.txt";
            FileWriter transactionWriter = new FileWriter(fileName, true); // Append to the file

            // Create a timestamp for the transaction
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long millis = System.currentTimeMillis();
            Date date = new Date(millis); // Use the current date and time
            String timestamp = dateFormat.format(date);

            // Create a transaction string to write to the file
            String transactionString = "Timestamp: " + timestamp + "\n" +
                    "Symbol: " + symbol + "\n" +
                    "Transaction Type: " + transactionType + "\n" +
                    "Quantity: " + quantity + "\n" +
                    "Share Price: " + sharePrice + "\n" +
                    "Transaction Amount: " + transactionAmount + "\n" +
                    "------------------------\n";

            // Write the transaction details to the file
            transactionWriter.write(transactionString);

            // Close the FileWriter
            transactionWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to save transaction details.");
        }
    }

    public static List<String> retrieveTransactionData(int userId) throws IOException {
        List<String> transactionDataList = new ArrayList<>();

        // Construct the file name based on the user ID
        String fileName = "user" + userId + "_transactions.txt";

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;

            while ((line = reader.readLine()) != null) {
                transactionDataList.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to retrieve transaction details.");
        }

        return transactionDataList;
    }

    static public void viewTransaction(int userId) {
        try {
            List<String> transactionData = retrieveTransactionData(userId);

            if (transactionData.isEmpty()) {
                System.out.println("No transaction data found for the user.");
            } else {
                System.out.println("Transaction Data for User " + userId + ":");
                for (String transaction : transactionData) {
                    System.out.println(transaction);
                }
            }

        } catch (IOException e) {
            System.out.println("--> First make any Transactions ");
        }
    }

    public static void View() {
        // Display user's details
        String userDetailsSQL = "SELECT * FROM user WHERE user_id = ?";
        String userPortfolioSQL = "SELECT * FROM user_portfolio WHERE user_id = ?";

        try {
            // Retrieve user details
            PreparedStatement userDetailsPst = con.prepareStatement(userDetailsSQL);
            userDetailsPst.setInt(1, id);
            ResultSet userDetailsRs = userDetailsPst.executeQuery();

            if (userDetailsRs.next()) {
                System.out.println("User Details:");
                System.out.println("User ID:       " + userDetailsRs.getInt("user_id"));
                System.out.println("Full Name:     " + userDetailsRs.getString("full_name"));
                System.out.println("Username:      " + userDetailsRs.getString("username"));
                System.out.println("Age:           " + userDetailsRs.getInt("age"));
                System.out.println("Balance:       " + userDetailsRs.getLong("amount"));
                System.out.println();
            }

            // Retrieve user's portfolio
            PreparedStatement userPortfolioPst = con.prepareStatement(userPortfolioSQL);
            userPortfolioPst.setInt(1, id);
            ResultSet userPortfolioRs = userPortfolioPst.executeQuery();

            if (userPortfolioRs.next()) {
                System.out.println("Portfolio:");
                System.out.printf("%-15s %-15s  %-20s%n", "Symbol", "Quantity Owned", "User's Investment");
                System.out.println("---------------------------------------------------------------");

                do {
                    System.out.printf("%-15s %-15s %-20s%n",
                            userPortfolioRs.getString("symbol"),
                            userPortfolioRs.getInt("quantity"),
                            userPortfolioRs.getDouble("investment"));
                } while (userPortfolioRs.next());
            } else {
                System.out.println("You don't own any shares.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to fetch user details or portfolio");
        }
        updateSharePricesInDatabase();
    }

    public static void registerUser() throws Exception {
        sc.nextLine();
        System.out.println("=== User Registration ===");

        System.out.print("Enter Your Full Name: ");
        String fullname = sc.nextLine();

        System.out.print("Enter Your User Name: ");
        String username = sc.nextLine();

        System.out.print("Enter Your Age: ");
        int age = sc.nextInt();
        sc.nextLine(); // Consume the newline character

        String password = "";

        while (true) {
            System.out.print("Enter Password: ");
            password = sc.nextLine();

            if (isPasswordValid(password)) {
                break;
            } else {
                System.out.println("Password Requirements:");
                System.out.println("- At least one uppercase letter");
                System.out.println("- At least one digit");
                System.out.println("- At least one special character");
                System.out.println("- Minimum length: 8 characters");
            }
        }

        System.out.print("Enter Your Initial Balance: $");
        long amount = sc.nextLong();

        PreparedStatement statement = con.prepareStatement(
                "INSERT INTO user (full_name, amount, username, age, password) VALUES (?, ?, ?, ?, ?)");
        statement.setString(1, fullname);
        statement.setLong(2, amount);
        statement.setString(3, username);
        statement.setInt(4, age);
        statement.setString(5, password);
        statement.executeUpdate();

        System.out.println("Registration successful! Welcome, " + fullname + "!");
    }

    public static boolean isPasswordValid(String password) {
        // Trim the password to remove leading and trailing spaces
        password = password.trim();

        // Use a regular expression to check the constraints
        String regex = "^(?=.*[A-Z])(?=.*\\d).{8,}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);

        return matcher.matches();
    }

    public static boolean authenticateUser(String username, String password) throws Exception {

        PreparedStatement statement = con.prepareStatement(
                "SELECT username FROM user WHERE username = ? AND password = ?");

        statement.setString(1, username);

        statement.setString(2, password);

        ResultSet resultSet = statement.executeQuery();

        return resultSet.next(); // True if a matching user is found, false otherwise

    }

}

class ShareDetails {
    private int quantity;
    private double investment;
    private String share_name;
    private double share_price;
    private String symbol;
    private long amount;
    private int id;
    private String username;

    public ShareDetails(int quantity, double investment, String company_name, double stockPrice, String symbol,
            long amount, int id) {
        this.quantity = quantity;
        this.investment = investment;
        this.share_name = company_name;
        this.share_price = stockPrice;
        this.symbol = symbol;
        this.amount = amount;
        this.id = id;

    }

    public ShareDetails(String symbol2, String shareName, double sharePrice, int quantity, double investment2) {
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setInvestment(double investment) {
        this.investment = investment;
    }

    public void setShare_name(String share_name) {
        this.share_name = share_name;
    }

    public void setShare_price(long share_price) {
        this.share_price = share_price;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getInvestment() {
        return investment;
    }

    public String getShare_name() {
        return share_name;
    }

    public long getShare_price() {
        return (long) share_price;
    }

    public String getSymbol() {
        return symbol;
    }

    public long getAmount() {
        return amount;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

}
