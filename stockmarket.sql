-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Aug 27, 2024 at 03:55 PM
-- Server version: 10.4.28-MariaDB
-- PHP Version: 8.0.28

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `stockmarket`
--

-- --------------------------------------------------------

--
-- Table structure for table `stockmarket`
--

CREATE TABLE `stockmarket` (
  `id` int(11) NOT NULL COMMENT 'yes',
  `symbol` varchar(20) NOT NULL,
  `company_name` varchar(20) NOT NULL,
  `industry` varchar(20) NOT NULL,
  `dividend` decimal(11,3) NOT NULL,
  `PE_ratio` decimal(11,3) NOT NULL,
  `EPS` decimal(11,3) NOT NULL,
  `weekly` decimal(11,3) NOT NULL,
  `monthly` decimal(11,3) NOT NULL,
  `yearly` decimal(11,3) NOT NULL,
  `price` decimal(11,3) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `stockmarket`
--

INSERT INTO `stockmarket` (`id`, `symbol`, `company_name`, `industry`, `dividend`, `PE_ratio`, `EPS`, `weekly`, `monthly`, `yearly`, `price`) VALUES
(1, 'RELIANCE', 'Reliance Industries ', 'Conglomerate', 0.440, 21.550, 64.980, 2497.560, 2523.890, 2450.230, 2697.958),
(2, 'TATASTEEL', 'Tata Steel Ltd.', 'Metals & Mining', 0.390, 6.780, 62.840, 1332.450, 1356.890, 1298.230, 152.151),
(3, 'ICICIBANK', 'ICICI Bank Ltd.', 'Banking', 0.240, 22.110, 12.450, 734.560, 748.900, 720.340, 1191.737),
(4, 'INFY', 'Infosys Ltd.', 'Technology', 0.290, 28.910, 4.560, 1579.450, 1602.890, 1540.230, 1580.121),
(5, 'HDFC', 'Housing Development ', 'Finance', 0.560, 23.670, 74.450, 2684.780, 2720.120, 2649.230, 1419.865),
(6, 'WIPRO', 'Wipro Ltd.', 'Technology', 0.250, 19.340, 3.780, 637.450, 648.900, 623.230, 666.334),
(7, 'HDFCBANK', 'HDFC Bank Ltd.', 'Banking', 0.150, 20.890, 61.450, 1532.560, 1550.900, 1500.230, 1357.253),
(8, 'ONGC', 'Oil and Natural Gas ', 'Energy', 0.690, 7.870, 15.450, 134.560, 138.900, 128.230, 129.457),
(9, 'AXISBANK', 'Axis Bank Ltd.', 'Banking', 0.180, 15.230, 22.450, 731.340, 747.780, 720.560, 636.618),
(10, 'BHARTIARTL', 'Bharti Airtel Ltd.', 'Telecommunications', 0.020, 16.890, 2.450, 708.230, 721.450, 695.670, 371.801),
(11, 'TCS', 'Tata Consultancy Ser', 'Technology', 0.300, 34.560, 10.450, 3865.230, 3912.780, 3790.450, 6420.437);

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE `user` (
  `user_id` int(11) NOT NULL COMMENT 'yes',
  `full_name` varchar(50) NOT NULL,
  `amount` bigint(11) NOT NULL,
  `username` varchar(11) NOT NULL,
  `age` int(11) NOT NULL,
  `password` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`user_id`, `full_name`, `amount`, `username`, `age`, `password`) VALUES
(5, 'Muskan Sumara', 48562, 'muskan', 19, '1234'),
(6, 'Tanya Khan', 90000, 'tk', 20, 'Tanyaa@1'),
(7, 'shreya', 789, 'shreya', 19, 'Shrey@25'),
(8, 'Via', 32092, 'via', 20, 'ViaSharma@35'),
(9, 'Archi shAH', 99997556, 'Archi ', 19, 'arcHI_1234');

-- --------------------------------------------------------

--
-- Table structure for table `user_portfolio`
--

CREATE TABLE `user_portfolio` (
  `id` int(11) NOT NULL COMMENT 'yes',
  `user_id` int(11) NOT NULL,
  `symbol` varchar(30) NOT NULL,
  `quantity` int(11) NOT NULL,
  `investment` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `user_portfolio`
--

INSERT INTO `user_portfolio` (`id`, `user_id`, `symbol`, `quantity`, `investment`) VALUES
(18, 5, 'ONGC', 3, 399.00),
(20, 5, 'TATASTEEL', 5, 1040.00),
(22, 7, 'WIPRO', 1, 717.00),
(23, 6, 'WIPRO', 2, 1342.00),
(24, 7, 'TCS', 2, 9836.00),
(28, 8, 'INFY', 1, 1541.00),
(30, 8, 'INFY', 1, 1541.00),
(32, 8, 'ICICIBANK', 2, 1584.00),
(34, 8, 'INFY', 1, 1541.00),
(36, 9, 'RELIANCE', 1, 2445.00);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `stockmarket`
--
ALTER TABLE `stockmarket`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`user_id`);

--
-- Indexes for table `user_portfolio`
--
ALTER TABLE `user_portfolio`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_user_portfolio_user` (`user_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `stockmarket`
--
ALTER TABLE `stockmarket`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'yes', AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT for table `user`
--
ALTER TABLE `user`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'yes', AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT for table `user_portfolio`
--
ALTER TABLE `user_portfolio`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'yes', AUTO_INCREMENT=38;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `user_portfolio`
--
ALTER TABLE `user_portfolio`
  ADD CONSTRAINT `fk_user_portfolio_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
