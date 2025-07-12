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
package com.remember5.junit.service.impl;

import cn.hutool.core.io.FileUtil;
import com.remember.common.entity.R;
import com.remember.common.entity.REnum;
import com.remember5.junit.service.IUploadService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * @author wangjiahao
 * @date 2023/3/16 11:03
 */
@Service
public class UploadServiceImpl implements IUploadService {
    @Override
    public R upload(MultipartFile file) {
        try {
            final File file1 = file.getResource().getFile();
            final File file2 = FileUtil.getTmpDir();
//            PdfBoxUtil.imgInPdf(file1, file2);

            return R.success();
        } catch (IOException e) {
//            throw new RuntimeException(e);
            return R.fail(REnum.A0001);
        }
    }
}
