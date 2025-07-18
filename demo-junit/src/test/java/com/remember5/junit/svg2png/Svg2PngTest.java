/**
 * Copyright [2022] [remember5]
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.remember5.junit.svg2png;

import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;

/**
 * @author wangjiahao
 * @date 2023/3/14 15:58
 */
@SpringBootTest
public class Svg2PngTest {


    @Test
    void testSvg2Png() {
        String base64Str = "data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAyMzEgMjMxIj48cGF0aCBkPSJNMzMuODMsMzMuODNhMTE1LjUsMTE1LjUsMCwxLDEsMCwxNjMuMzQsMTE1LjQ5LDExNS40OSwwLDAsMSwwLTE2My4zNFoiIHN0eWxlPSJmaWxsOiNmZmU1MDA7Ii8+PHBhdGggZD0ibTExNS41IDUxLjc1YTYzLjc1IDYzLjc1IDAgMCAwLTEwLjUgMTI2LjYzdjE0LjA5YTExNS41IDExNS41IDAgMCAwLTUzLjcyOSAxOS4wMjcgMTE1LjUgMTE1LjUgMCAwIDAgMTI4LjQ2IDAgMTE1LjUgMTE1LjUgMCAwIDAtNTMuNzI5LTE5LjAyOXYtMTQuMDg0YTYzLjc1IDYzLjc1IDAgMCAwIDUzLjI1LTYyLjg4MSA2My43NSA2My43NSAwIDAgMC02My42NS02My43NSA2My43NSA2My43NSAwIDAgMC0wLjA5OTYxIDB6IiBzdHlsZT0iZmlsbDojZWY5ODMxOyIvPjxwYXRoIGQ9Im0xNDEuNzUgMTk1YTExNC43OSAxMTQuNzkgMCAwIDEgMzggMTYuNSAxMTUuNTMgMTE1LjUzIDAgMCAxLTEyOC40NiAwIDExNC43OSAxMTQuNzkgMCAwIDEgMzgtMTYuNWwxNS43MSAxNS43NWgyMXoiIHN0eWxlPSJmaWxsOiMzNTRCNjU7Ii8+PHBhdGggZD0ibTg5LjI5MSAxOTVhMTE0Ljc5IDExNC43OSAwIDAgMC0zOC4wMDIgMTYuNSAxMTUuNTMgMTE1LjUzIDAgMCAwIDM4LjAwMiAxNi40ODJ6bTUyLjQzNCAwdjMyLjk4MmExMTUuNTMgMTE1LjUzIDAgMCAwIDM4LTE2LjQ4MiAxMTQuNzkgMTE0Ljc5IDAgMCAwLTM4LTE2LjV6IiBzdHlsZT0iZmlsbDojM0Q4RUJCOyIvPjxwYXRoIGQ9Im0xNTcuMTUgMTk5Ljc1YzAuMjU0OCA3LjQ1MDEgMS41NCAxNC44NTUgNC45NTEyIDIxLjQzMmExMTUuNTMgMTE1LjUzIDAgMCAwIDE3LjYxOS05LjY3OTcgMTE0Ljc5IDExNC43OSAwIDAgMC0yMi41Ny0xMS43NTJ6bS04My4yOTUgMmUtM2ExMTQuNzkgMTE0Ljc5IDAgMCAwLTIyLjU3IDExLjc1IDExNS41MyAxMTUuNTMgMCAwIDAgMTcuNjIxIDkuNjc5N2MzLjQxMS02LjU3NjUgNC42OTQ0LTEzLjk4IDQuOTQ5Mi0yMS40M3oiIHN0eWxlPSJmaWxsOiM4OUQwREE7Ii8+PHBhdGggZD0ibTk5LjE5NyAyMDQuOTd2MmUtM2wxNi4zMDIgMTYuMzAxIDE2LjMwMS0xNi4zMDF2LTJlLTN6IiBzdHlsZT0iZmlsbDojMDBGRkZEOyIvPjxwYXRoIGQ9Im0xNjkuNjUgOTAuOTk4YzMuMTM3IDExLjk0IDQuOTM3MSAzNi40ODQtMy40MTE4IDU4LjIxM2w1LjEyOSAzLjExNjRjMTAuMDQ0LTE1LjE5OSAxNC45NTktMzkuMTYzIDEzLjk0My02MS4zM3oiIHN0eWxlPSJmaWxsOiMwMDA7Ii8+PHBhdGggZD0ibTQ1LjA4MSA5MC45ODljLTAuODgwODUgNC45MzA0LTAuODc1MzQgMTQuOTUzLTAuMTUwMjcgMjEuNzUgMi4xMzE4IDE5Ljk4IDE2LjY3MSA0Mi41MDUgMTYuNjcxIDQyLjUwNWw1LjczNTItNC40MzMxcy0xMy4yNDQtMzEuMzQ4LTYuMDU3MS01Mi43NTFjMC41MjEwOC0xLjU1MTcgMC45NTU5Mi0yLjkxNiAxLjM0NjItNC4xODM1eiIgc3R5bGU9ImZpbGw6IzAwMDsiLz48cGF0aCBkPSJtMTE3IDMuNDg4M2MtOC4yMTM2LTAuMTk4ODctMTkuMTMgNy45MzMtMTguNDk0IDkuMzUxNiAxLjYyMTQgMy42MTg2IDExLjE3NiAyMi41NSAxMS44ODkgMjMuOTYzaDEwLjE0OGMyLjYwMjItNi4zMTAyIDExLjMyLTI2LjUzMSAxMS4zMi0yNi41MzFzLTQuMTM4Mi00LjEzOC0xMi40MTYtNi40Mzc1Yy0wLjc3NjA1LTAuMjE1NTYtMS41OTc2LTAuMzI1MTMtMi40NDczLTAuMzQ1N3oiIHN0eWxlPSJmaWxsOm5vbmU7Ii8+PHBhdGggZD0ibTExNS45NSA0LjU0MjhjLTMuMTU2MyAwLTYuMzEyMyAwLjU3NDYyLTkuMjE2NSAxLjcxNS01LjgwODQgMi4yODE3LTEwLjUzMiA2LjgwOC0xMi43NzkgMTIuMjQ1di01ZS0zYy0xLjgxNjYgNC4zOTctMi4wMjMzIDkuMzQ0MS0wLjU4MDU4IDEzLjg1NyAwLjY5MzUyIDIuMTY4NyAxLjc2OTMgNC4yMjk2IDMuMTUzMyA2LjA5NjhoMzguODkzYzAuNzEwMzItMC45NTc2OSAxLjM0NDEtMS45NjQxIDEuODc4Ny0zLjAxNDQgMi42ODExLTUuMjY3MyAyLjkyOTYtMTEuNTQyIDAuNjcyNTMtMTYuOTc1LTIuMjU3LTUuNDMzNy02Ljk4OTMtOS45NTIyLTEyLjgwMi0xMi4yMjQtMi45MDY0LTEuMTMzNS02LjA2MzMtMS42OTg3LTkuMjE5Ni0xLjY5NTZ6IiBzdHlsZT0iZmlsbDojMDAwOyIvPjxwYXRoIGQ9Im05Mi41MTIgMjguMTI1YzAuMTMzODcgMS40MzE4IDAuNDE4NzcgMi44NTExIDAuODU5NjIgNC4yMzA2IDEuNDQyOSA0LjUxMjcgNC41Mjc4IDguNTY1NCA4LjY0MTEgMTEuMzUzIDQuMTEzNSAyLjc4NzMgOS4yMzExIDQuMjkxMyAxNC4zMzYgNC4yMTY1IDUuMTA1Mi0wLjA3NjQgMTAuMTY4LTEuNzMzMyAxNC4xODEtNC42NDE5IDIuODc1NC0yLjA4MzQgNS4yMTMyLTQuNzkzMiA2Ljc2NjUtNy44NDQ3IDEuMjAwNS0yLjM1ODYgMS45MDg1LTQuOTE4OCAyLjEyNy03LjUxNTYtMTUuMDM3LTIuNjQwNy0zMS40MjEtMy40NjcxLTQ2LjkxMiAwLjIwMjUzeiIgc3R5bGU9ImZpbGw6I2ZmNGU0ZTsiLz48cGF0aCBkPSJtMzQuNDI2IDkwLjYzYzE0LjcxNCA0LjA3NzkgMjIuNjgzIDYuNDA4NSA0NS4yNTQgNy40MjU3IDIuNTMxOC0xOC4xODUgNC42Njg5LTI4LjY3MiAxMC4wMjMtMzguMzUyIDMuMjAyNSAxMy40MDMgMy44MzQ2IDI1LjIyIDIuOTEwNiA0Mi4yNTNsMTEuMTcyLTAuMjMxNjFjMS40NzA2LTExLjg4NiAzLjg5ODktMjkuMjEzIDIuMTYzNi00Mi4wMjEgMTAuNDE2IDEyLjYzMSAxMS4zNzMgMjMuNjI0IDEzLjA3NyAzOS43MjYgMzAuMTc0LTAuNzYwMDQgNTkuODA4LTQuNTEyMSA3Ny44NDUtMTAuMTI4LTEwLjc2LTM4LjYwOC00MS40NzUtNTUuNjYtODAuMzgtNTYuMTA0LTM4LjE4Mi0wLjQ1MTM0LTc0LjU0MyAyMi40MDUtODIuMDY1IDU3LjQzMnoiIHN0eWxlPSJmaWxsOiMwMDA7Ii8+PHBhdGggZD0ibTc4LjczIDExMWExMC45IDEwLjkgMCAwIDEgMTUuMTkgMG00My4xNiAwYTEwLjkgMTAuOSAwIDAgMSAxNS4xOSAwIiBzdHlsZT0iZmlsbDpub25lO3N0cm9rZS1saW5lY2FwOnJvdW5kO3N0cm9rZS1saW5lam9pbjpyb3VuZDtzdHJva2Utd2lkdGg6Ni4xOTk5cHg7c3Ryb2tlOiMwMDA7Ii8+PHBhdGggZD0ibTc5LjgwNCAxMjMuNzRoNy4wN201Ny4yNzMgMGg3LjA1IiBzdHlsZT0iZmlsbDpub25lO3N0cm9rZS1saW5lY2FwOnJvdW5kO3N0cm9rZS1saW5lam9pbjpyb3VuZDtzdHJva2Utd2lkdGg6NS45OTk4cHg7c3Ryb2tlOiMwMDcyZmY7Ii8+PHBhdGggZD0ibTEyNi4yOCAxNDkuODJjLTYuMTYgMi40My0xNS41MiAyLjQyLTIxLjU2IDAiIHN0eWxlPSJmaWxsOm5vbmU7c3Ryb2tlLWxpbmVjYXA6cm91bmQ7c3Ryb2tlLWxpbmVqb2luOnJvdW5kO3N0cm9rZS13aWR0aDo1Ljk5OThweDtzdHJva2U6IzAwMDsiLz48L3N2Zz4=";
        // base64 2 file

        final File file = Base64.decodeToFile(base64Str, new File("/Users/wangjiahao/Downloads/test.svg"));

    }

    public static void main(String[] args) {

        String key = "_JiaMi_XIADAN_MIYAO_@9527#_";
        SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, key.getBytes());
        System.err.println(aes.encryptHex("hello"));
    }
}
