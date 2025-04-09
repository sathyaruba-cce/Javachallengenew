CREATE DATABASE LoanManagementSystem;
USE LoanManagementSystem;
CREATE TABLE Customer (
    customerId INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phoneNumber VARCHAR(15),
    address VARCHAR(255),
    creditScore INT NOT NULL
);
desc Customer;
CREATE TABLE Loan (
    loanId INT PRIMARY KEY,
    customerId INT,
    principalAmount DECIMAL(15,2) NOT NULL,
    interestRate DECIMAL(5,2) NOT NULL,
    loanTerm INT NOT NULL,
    loanType VARCHAR(20),   
    loanStatus VARCHAR(20), 

    FOREIGN KEY (customerId) REFERENCES Customer(customerId)
);
CREATE TABLE HomeLoan (
    loanId INT PRIMARY KEY,
    propertyAddress VARCHAR(255),
    propertyValue INT,

    FOREIGN KEY (loanId) REFERENCES Loan(loanId)
);
CREATE TABLE CarLoan (
    loanId INT PRIMARY KEY,
    carModel VARCHAR(100),
    carValue INT,

    FOREIGN KEY (loanId) REFERENCES Loan(loanId)
);
INSERT INTO Customer VALUES
(1, 'Arun Kumar', 'arun.kumar@gmail.com', '9876543210', 'Chennai, TN', 720),
(2, 'Meena R', 'meena.r@yahoo.com', '9845098765', 'Coimbatore, TN', 680),
(3, 'Ravi Shankar', 'ravi.s@gmail.com', '9945012345', 'Madurai, TN', 710),
(4, 'Divya Lakshmi', 'divya.lk@yahoo.in', '9566234567', 'Salem, TN', 640),
(5, 'Vignesh V', 'vigneshv@gmail.com', '9845111222', 'Tirunelveli, TN', 780),
(6, 'Priya Dharshini', 'priya.dharshini@gmail.com', '9003287765', 'Trichy, TN', 700),
(7, 'Sathish K', 'sathish.k@gmail.com', '9798098770', 'Erode, TN', 600),
(8, 'Lakshmi Narayanan', 'lakshmi.n@gmail.com', '9823421098', 'Kanchipuram, TN', 675),
(9, 'Harini S', 'harini.s@gmail.com', '9090012345', 'Thanjavur, TN', 740),
(10, 'Mohan Raj', 'mohan.raj@gmail.com', '9654210900', 'Vellore, TN', 690);
INSERT INTO Loan VALUES
(201, 1, 500000.00, 7.5, 60, 'HomeLoan', 'Pending'),
(202, 2, 300000.00, 8.2, 48, 'CarLoan', 'Pending'),
(203, 3, 600000.00, 6.9, 72, 'HomeLoan', 'Pending'),
(204, 4, 250000.00, 9.0, 36, 'CarLoan', 'Pending'),
(205, 5, 450000.00, 7.0, 60, 'HomeLoan', 'Pending'),
(206, 6, 350000.00, 8.0, 48, 'CarLoan', 'Pending'),
(207, 7, 500000.00, 7.2, 60, 'HomeLoan', 'Pending'),
(208, 8, 280000.00, 8.5, 36, 'CarLoan', 'Pending'),
(209, 9, 750000.00, 6.5, 84, 'HomeLoan', 'Pending'),
(210, 10, 320000.00, 8.1, 48, 'CarLoan', 'Pending');
INSERT INTO HomeLoan VALUES
(201, 'No.12, Anna Nagar, Chennai', 520000),
(203, '7th Street, KK Nagar, Madurai', 630000),
(205, '14/3 Mettur Road, Tirunelveli', 470000),
(207, 'Opp. Railway Station, Salem', 510000),
(209, '17A Srirangam Main Road, Thanjavur', 790000);
INSERT INTO CarLoan VALUES
(202, 'Hyundai i20', 320000),
(204, 'Tata Nexon', 260000),
(206, 'Maruti Swift Dzire', 370000),
(208, 'Honda Amaze', 300000),
(210, 'Renault Kwid', 350000);

