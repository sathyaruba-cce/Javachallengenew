package com.loan.main;
import com.loan.dao.ILoanRepository;
import com.loan.dao.ILoanRepositoryImpl;
import com.loan.entity.Customer;
import com.loan.entity.Loan;
import com.loan.entity.HomeLoan;
import com.loan.entity.CarLoan;
import com.loan.exception.InvalidLoanException;

import java.util.*;

public class LoanManagement {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ILoanRepository repo = new ILoanRepositoryImpl();

        while (true) {
            System.out.println("\n=== Loan Management System ===");
            System.out.println("1. Apply Loan");
            System.out.println("2. Get All Loans");
            System.out.println("3. Get Loan by ID");
            System.out.println("4. Repay Loan");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            int choice = sc.nextInt();
            sc.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter Loan ID: ");
                    int loanId = sc.nextInt();
                    sc.nextLine();

                    System.out.print("Enter Customer ID: ");
                    int customerId = sc.nextInt();
                    sc.nextLine();

                    System.out.print("Enter Customer Name: ");
                    String name = sc.nextLine();

                    System.out.print("Enter Credit Score: ");
                    int creditScore = sc.nextInt();

                    System.out.print("Enter Loan Type (Car/Home): ");
                    String type = sc.next();
                    sc.nextLine();

                    System.out.print("Enter Principal Amount: ");
                    double principal = sc.nextDouble();

                    System.out.print("Enter Interest Rate: ");
                    double rate = sc.nextDouble();

                    System.out.print("Enter Loan Term (in months): ");
                    int term = sc.nextInt();
                    sc.nextLine();

                    Customer customer = new Customer();
                    customer.setCustomerId(customerId);
                    customer.setName(name);
                    customer.setCreditScore(creditScore);

                    Loan loan = null;

                    if (type.equalsIgnoreCase("Car")) {
                        System.out.print("Enter Car Model: ");
                        String model = sc.nextLine();
                        System.out.print("Enter Car Price: ");
                        int price = sc.nextInt();

                        loan = new CarLoan(loanId, customer, principal, rate, term, "Car", "Pending", model, price);

                    } else if (type.equalsIgnoreCase("Home")) {
                        System.out.print("Enter Property Location: ");
                        String location = sc.nextLine();
                        System.out.print("Enter Property Value: ");
                        int  value = sc.nextInt();

                        loan = new HomeLoan(loanId, customer, principal, rate, term, "Home", "Pending", location, value);
                    }

                    if (loan != null) {
                        repo.applyLoan(loan);
                    } else {
                        System.out.println("Invalid loan type.");
                    }
                    break;
                    

                case 2:
                
                    List<Loan> allLoans = repo.getAllLoan();
                    if (allLoans.isEmpty()) {
                        System.out.println("No loans found.");
                    } else {
                        System.out.println("All loan information....");
                        for (Loan loans : allLoans) {
                            System.out.println(loans); // Print loan details
                        
                    }
                }
                    break;


                case 3:
                    System.out.print("Enter Loan ID: ");
                    int searchId = sc.nextInt();
                    try {
                        repo.getLoanById(searchId);
                    } catch (InvalidLoanException e) {
                        System.out.println(e.getMessage());
                    }
                    break;

                case 4:
                    System.out.print("Enter Loan ID: ");
                    int repayId = sc.nextInt();
                    System.out.print("Enter repayment amount: ");
                    double amount = sc.nextDouble();
                    repo.loanRepayment(repayId, amount);
                    break;

                case 5:
                    System.out.println("Exiting application...");
                    System.exit(0);

                default:
                    System.out.println("Invalid choice. Please try again.");
                sc.close();
            }
        }
    }
}
