package com.wap.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/account")
public class AccountController {

	@RequestMapping("list")
	public ModelAndView list() {
		ModelAndView view = new ModelAndView("account/list");
		return view;

	}
}
