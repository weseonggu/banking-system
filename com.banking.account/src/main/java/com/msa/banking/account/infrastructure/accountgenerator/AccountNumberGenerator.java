package com.msa.banking.account.infrastructure.accountgenerator;

import java.util.Random;

public class AccountNumberGenerator {

    public static String generateAccountNumber() {
        Random random = new Random();

        // 첫 번째 부분: 3자리 숫자
        int part1 = random.nextInt(900) + 100;  // 100부터 999까지의 숫자

        // 두 번째 부분: 4자리 숫자
        int part2 = random.nextInt(9000) + 1000;  // 1000부터 9999까지의 숫자

        // 세 번째 부분: 7자리 숫자
        int part3 = random.nextInt(9000000) + 1000000;  // 1000000부터 9999999까지의 숫자

        // 각 부분을 '-'로 연결하여 계좌번호 형식 만들기
        return String.format("%03d-%04d-%07d", part1, part2, part3);
    }

    public static void main(String[] args) {
        String randomAccountNumber = generateAccountNumber();
        System.out.println("Generated Account Number: " + randomAccountNumber);
    }
}