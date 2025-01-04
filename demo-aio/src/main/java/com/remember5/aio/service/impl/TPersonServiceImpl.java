package com.remember5.aio.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.remember5.aio.mapper.TPersonMapper;
import com.remember5.aio.domain.TPerson;
import com.remember5.aio.service.TPersonService;
@Service
public class TPersonServiceImpl extends ServiceImpl<TPersonMapper, TPerson> implements TPersonService{

}
