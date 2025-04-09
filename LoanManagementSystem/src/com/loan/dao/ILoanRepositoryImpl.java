package com.loan.dao;
import java.util.*;
import com.loan.entity.*;
import com.loan.exception.InvalidLoanException;
import com.loan.util.DbConUtil;


import java.sql.*;


public class ILoanRepositoryImpl implements ILoanRepository {

    Scanner sc = new Scanner(System.in);

    @Override
    public void applyLoan(Loan loan) {
        System.out.print("Do you want to apply for this loan? (Yes/No): ");
        String confirm = sc.nextLine();

        if (!confirm.equalsIgnoreCase("Yes")) {
            System.out.println("Loan application cancelled by user.");
            return;
        }

        Connection conn = DbConUtil.getDbConnection();
        PreparedStatement psLoan = null, psSub = null;
        boolean transactionSuccess = false;

        try {
            conn.setAutoCommit(false);

            String loanQuery = "INSERT INTO Loan (loanId, loanType, name, principalAmount, interestRate, loanTerm, creditScore, loanStatus) " +
                               "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            psLoan = conn.prepareStatement(loanQuery);
            psLoan.setInt(1, loan.getLoanId());
            psLoan.setString(2, loan.getLoanType());
            psLoan.setString(3, loan.getCustomer().getName());
            psLoan.setDouble(4, loan.getPrincipalAmount());
            psLoan.setDouble(5, loan.getInterestRate());
            psLoan.setInt(6, loan.getLoanTerm());
            psLoan.setInt(7, loan.getCustomer().getCreditScore());
            psLoan.setString(8, loan.getLoanStatus());

            int rows = psLoan.executeUpdate();

            if (rows > 0) {
                if (loan instanceof CarLoan) {
                    CarLoan cl = (CarLoan) loan;
                    String carQuery = "INSERT INTO CarLoan (loanId, carModel, carValue) VALUES (?, ?, ?)";
                    psSub = conn.prepareStatement(carQuery);
                    psSub.setInt(1, cl.getLoanId());
                    psSub.setString(2, cl.getCarModel());
                    psSub.setDouble(3, cl.getCarValue());
                    psSub.executeUpdate();
                } else if (loan instanceof HomeLoan) {
                    HomeLoan hl = (HomeLoan) loan;
                    String homeQuery = "INSERT INTO HomeLoan (loanId, propertyAddress, propertyValue) VALUES (?, ?, ?)";
                    psSub = conn.prepareStatement(homeQuery);
                    psSub.setInt(1, hl.getLoanId());
                    psSub.setString(2, hl.getPropertyAddress());
                    psSub.setDouble(3, hl.getPropertyValue());
                    psSub.executeUpdate();
                }

                conn.commit();
                transactionSuccess = true;
                System.out.println("Loan application submitted successfully!");
            }

        } catch (SQLException e) {
            System.out.println("Error applying loan: " + e.getMessage());
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                System.out.println("Error rolling back transaction: " + rollbackEx.getMessage());
            }
        } finally {
            try {
                if (psLoan != null) psLoan.close();
                if (psSub != null) psSub.close();
                if (conn != null) {
                    if (!transactionSuccess) {
                        conn.rollback();
                    }
                    conn.close();
                }
            } catch (SQLException ignored) {}
        }
    }

    @Override
    public double calculateInterest(int loanId) throws InvalidLoanException {
        Connection conn = DbConUtil.getDbConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        double interest = 0.0;

        try {
            String sql = "SELECT principalAmount, interestRate, loanTerm FROM Loan WHERE loanId = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, loanId);
            rs = ps.executeQuery();

            if (rs.next()) {
                double principal = rs.getDouble("principalAmount");
                double rate = rs.getDouble("interestRate");
                int term = rs.getInt("loanTerm");

                interest = calculateInterest(principal, rate, term);
            } else {
                throw new InvalidLoanException("Loan with ID " + loanId + " not found.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new InvalidLoanException("Error calculating interest: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException ignored) {}
        }

        return interest;
    }

    @Override
    public double calculateInterest(double principal, double rate, int term) {
        return (principal * rate * term) / 12 / 100;
    }
    
    
  
    @Override
    public String loanStatus(int loanId) {
        Connection conn = DbConUtil.getDbConnection();
        PreparedStatement psSelect = null;
        PreparedStatement psUpdate = null;
        ResultSet rs = null;
        String resultMessage = "";

        try {
            String selectQuery = "SELECT creditScore FROM Loan WHERE loanId = ?";
            psSelect = conn.prepareStatement(selectQuery);
            psSelect.setInt(1, loanId);
            rs = psSelect.executeQuery();

            if (rs.next()) {
                int creditScore = rs.getInt("creditScore");
                String status;

                if (creditScore > 650) {
                    status = "Approved";
                    resultMessage = "Loan ID " + loanId + " is Approved.";
                } else {
                    status = "Rejected";
                    resultMessage = "Loan ID " + loanId + " is Rejected due to low credit score.";
                }

                String updateQuery = "UPDATE Loan SET status = ? WHERE loanId = ?";
                psUpdate = conn.prepareStatement(updateQuery);
                psUpdate.setString(1, status);
                psUpdate.setInt(2, loanId);
                psUpdate.executeUpdate();

            } else {
                resultMessage = "Loan ID " + loanId + " not found.";
            }

        } catch (SQLException e) {
            e.printStackTrace();
            resultMessage = "Error occurred while checking loan status.";
        } finally {
            try {
                if (rs != null) rs.close();
                if (psSelect != null) psSelect.close();
                if (psUpdate != null) psUpdate.close();
                if (conn != null) conn.close();
            } catch (SQLException ignored) {}
        }

        return resultMessage;
    }
    
    
    
    
    @Override
    public double calculateEMI(int loanId) throws InvalidLoanException {
        Connection conn = DbConUtil.getDbConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        double emi = 0.0;

        try {
            String sql = "SELECT principalAmount, interestRate, loanTerm FROM Loan WHERE loanId = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, loanId);
            rs = ps.executeQuery();

            if (rs.next()) {
                double principal = rs.getDouble("principalAmount");
                double annualRate = rs.getDouble("interestRate");
                int term = rs.getInt("loanTerm");

                emi = calculateEMI(principal, annualRate, term);
            } else {
                throw new InvalidLoanException("Loan with ID " + loanId + " not found.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new InvalidLoanException("Error while calculating EMI: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException ignored) {}
        }

        return emi;
    }

    @Override
    public double calculateEMI(double principal, double annualRate, int termMonths) {
        double monthlyRate = annualRate / 12 / 100;

        if (monthlyRate == 0) {
            return principal / termMonths; // No interest case
        }

        double numerator = principal * monthlyRate * Math.pow(1 + monthlyRate, termMonths);
        double denominator = Math.pow(1 + monthlyRate, termMonths) - 1;

        return numerator / denominator;
    }

    
    
    
    @Override
    public void loanRepayment(int loanId, double amount) {
        Connection conn = DbConUtil.getDbConnection();
        PreparedStatement psSelect = null;
        PreparedStatement psUpdate = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT principalAmount, interestRate, loanTerm FROM Loan WHERE loanId = ?";
            psSelect = conn.prepareStatement(sql);
            psSelect.setInt(1, loanId);
            rs = psSelect.executeQuery();

            if (rs.next()) {
                double principal = rs.getDouble("principalAmount");
                double rate = rs.getDouble("interestRate");
                int term = rs.getInt("loanTerm");

                double emi = calculateEMI(principal, rate, term);

                if (amount < emi) {
                    System.out.println("Repayment amount is less than 1 EMI. Payment rejected.");
                    return;
                }

                int paidEmis = (int) (amount / emi);
                int remainingTerm = term - paidEmis;
                if (remainingTerm < 0) remainingTerm = 0;

                String updateQuery = "UPDATE Loan SET loanTerm = ? WHERE loanId = ?";
                psUpdate = conn.prepareStatement(updateQuery);
                psUpdate.setInt(1, remainingTerm);
                psUpdate.setInt(2, loanId);
                psUpdate.executeUpdate();

                System.out.println("Payment accepted. EMIs paid: " + paidEmis +
                                   ", Remaining EMIs: " + remainingTerm);

            } else {
                System.out.println("Loan with ID " + loanId + " not found.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error during loan repayment.");
        } finally {
            try {
                if (rs != null) rs.close();
                if (psSelect != null) psSelect.close();
                if (psUpdate != null) psUpdate.close();
                if (conn != null) conn.close();
            } catch (SQLException ignored) {}
        }
    }


    
   
    @Override
    public List<Loan> getAllLoan() {
        List<Loan> loanList = new ArrayList<>();

        Connection conn = DbConUtil.getDbConnection();
        PreparedStatement psLoan = null;
        ResultSet rsLoan = null;

        try {
            String loanQuery = "SELECT * FROM Loan";
            psLoan = conn.prepareStatement(loanQuery);
            rsLoan = psLoan.executeQuery();

            if (!rsLoan.isBeforeFirst()) { // Check if no records are found
                System.out.println("No loans found in the database.");
            }

            while (rsLoan.next()) {
                int loanId = rsLoan.getInt("loanId");
                String loanType = rsLoan.getString("loanType");
                double principal = rsLoan.getDouble("principalAmount");
                double rate = rsLoan.getDouble("interestRate");
                int term = rsLoan.getInt("loanTerm");
                String status = rsLoan.getString("loanStatus");
                int customerId = rsLoan.getInt("customerId");

                Customer customer = new Customer();
                customer.setCustomerId(customerId);

                Loan loan = null;
                PreparedStatement psSub = null;
                ResultSet rsSub = null;

                if (loanType.equalsIgnoreCase("Car")) {
                    String carQuery = "SELECT * FROM CarLoan WHERE loanId = ?";
                    psSub = conn.prepareStatement(carQuery);
                    psSub.setInt(1, loanId);
                    rsSub = psSub.executeQuery();

                    if (rsSub.next()) {
                        String model = rsSub.getString("carModel");
                        int price = rsSub.getInt("carPrice");

                        loan = new CarLoan(loanId, customer, principal, rate, term, loanType, status, model, price);
                    }

                } else if (loanType.equalsIgnoreCase("Home")) {
                    String homeQuery = "SELECT * FROM HomeLoan WHERE loanId = ?";
                    psSub = conn.prepareStatement(homeQuery);
                    psSub.setInt(1, loanId);
                    rsSub = psSub.executeQuery();

                    if (rsSub.next()) {
                        String location = rsSub.getString("propertyLocation");
                        int value = rsSub.getInt("propertyValue");

                        loan = new HomeLoan(loanId, customer, principal, rate, term, loanType, status, location, value);
                    }
                }

                if (loan != null) {
                    loanList.add(loan);
                    System.out.println("Loan details fetched: " + loan);
                }

                if (rsSub != null) rsSub.close();
                if (psSub != null) psSub.close();
            }

        } catch (SQLException e) {
            System.out.println("Error fetching loans: " + e.getMessage());
        } finally {
            try {
                if (rsLoan != null) rsLoan.close();
                if (psLoan != null) psLoan.close();
                if (conn != null) conn.close();
            } catch (SQLException ignored) {}
        }

        return loanList;
    }


    
    
    
    
    
    
    @Override
    public Loan getLoanById(int loanId) throws InvalidLoanException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Loan loan = null;

        try {
            conn = DbConUtil.getDbConnection();
            String query = "SELECT * FROM Loan WHERE loanId = ?";
            ps = conn.prepareStatement(query);
            ps.setInt(1, loanId);
            rs = ps.executeQuery();

            if (rs.next()) {
                loan = new Loan();
                loan.setLoanId(rs.getInt("loanId"));
                loan.setLoanType(rs.getString("loanType"));
                loan.setPrincipalAmount(rs.getDouble("principalAmount"));
                loan.setInterestRate(rs.getDouble("interestRate"));
                loan.setLoanTerm(rs.getInt("loanTerm"));
                loan.setLoanStatus(rs.getString("loanStatus"));

                
                System.out.println("Loan Details:");
                System.out.println("Loan ID       : " + loan.getLoanId());
                System.out.println("Loan Type     : " + loan.getLoanType());
                System.out.println("Principal     : ₹" + loan.getPrincipalAmount());
                System.out.println("Interest Rate : " + loan.getInterestRate() + "%");
                System.out.println("Loan Term     : " + loan.getLoanTerm() + " months");
                System.out.println("Status        : " + loan.getLoanStatus());

            } else {
                throw new InvalidLoanException("Loan with ID " + loanId + " not found.");
            }

        } catch (SQLException e) {
            throw new InvalidLoanException("Error retrieving loan: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException ignored) {}
        }

        return loan;
    }

    
    
    
}
