package com.loan.dao;
import com.loan.entity.Loan;
import com.loan.exception.InvalidLoanException;

import java.util.List;

public interface ILoanRepository {
	void applyLoan(Loan loan); 
	double calculateInterest(int loanId) throws InvalidLoanException;
    double calculateInterest(double principal, double rate, int term);
    String loanStatus(int loanId);
    double calculateEMI(int loanId) throws InvalidLoanException;
    double calculateEMI(double principal, double rate, int term);
    void loanRepayment(int loanId, double amount);
    List<Loan> getAllLoan();
    Loan getLoanById(int loanId) throws InvalidLoanException;
}
