package tw.group5.shopping.controller;

import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import tw.group5.order.model.Order;
import tw.group5.shopping.model.ShoppingCartItem;
import tw.group5.shopping.sevice.ShoppingCartItemService;

@Controller
@SessionAttributes(names = {"totalWithCoupon","shoppingCartItems","orderNo","tradeDate","CheckMacValue","shippingFee","totalWithDeliveryFee","order","shippingType","storeName","storeAddress","payType"})
@RequestMapping(path = "/group5/user")
public class PayAndDeliverController {
	
	@Autowired
	private ShoppingCartItemService sCIService;
	
	private List<ShoppingCartItem> shoppingCartItems;
	
	
	//接到結帳頁面Step2:選擇付款及運送方式
	@PostMapping(path = "/shopping.cart/pay_and_delivery")
	public String processPayActionStep2(@RequestParam("voucherNo")String voucherNo, @RequestParam("voucherDiscount")int voucherDiscount, @RequestParam("totalWithCoupon")int totalWithCoupon, Model m) {
		
		m.addAttribute("totalWithCoupon", totalWithCoupon);
		
		//new一個order物件，開始放入一些屬性，先放到session，到最後資料齊了再存入SQL
		Order order = new Order();
	
		if (voucherNo.equals("NULL")) {	
		}else {
			order.setVoucherNo(voucherNo);
		}
		order.setVoucherDiscount(voucherDiscount);
		
		m.addAttribute("order", order);
		
		
		return "shopping/pay_and_delivery";
	}
		
	
		
	//接到結帳頁面Step3:在後台先產生訂單號，並跳到Step3頁面請客人填資料
	@PostMapping(path = "/shopping.cart/pay/orderInformation")
	public String processPayActionStep3(@RequestParam("formPayType")String payType, @RequestParam("formShippingType")String shippingType,
										@RequestParam("formShippingFee")String shippingFee,
										@RequestParam("formTotalWithDeliveryFee")int totalWithDeliveryFee, Model m) throws NoSuchAlgorithmException {
				
		String account = SecurityContextHolder.getContext().getAuthentication().getName();
		
		shoppingCartItems = sCIService.findAllShoppingCartItems(account);
		m.addAttribute("shoppingCartItems", shoppingCartItems);
		
		//算出商品的總金額
		int  commoditySubtotal = 0;
		for (ShoppingCartItem shoppingCartItem : shoppingCartItems) {
			commoditySubtotal += shoppingCartItem.getSubtotal();
		}
		
			
		//*產生訂單編號放入session
		String orderNo = "iFit";
		//取當天年月日
		DateFormat format = new SimpleDateFormat("yyMMdd");
		Date now = new Date();
		String date = format.format(now);
		orderNo += date;
		//再配上UUID碼
		String orderNoUUID = UUID.randomUUID().toString().substring(0,7);
		orderNo += orderNoUUID;
		m.addAttribute("orderNo",orderNo);
			
			
		//*抓當下時間作為交易時間放入session
		DateFormat tradeForm = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String tradeDate = tradeForm.format(now);
		m.addAttribute("tradeDate",tradeDate);
			
			
		//*綠界檢核碼
		//1.先串出Query String
		String queryStr = "ChoosePayment=Credit&EncryptType=1&ItemName=iFit線上購物&MerchantID=3002607&MerchantTradeDate="+
	               tradeDate+"&MerchantTradeNo="+orderNo+"&OrderResultURL=http://localhost:8080/group5/user/order_result&PaymentType=aio&ReturnURL=http://localhost:8080/group5/user/order_result&TotalAmount="+
	               totalWithDeliveryFee+"&TradeDesc=test Description";
		//2.最前方加入 HashKey，最後方加入 HashIV
		queryStr = "HashKey=pwFHCqoQZGmho4w6&"+ queryStr + "&HashIV=EkRm7iFT261dpevs";
		//3.做 URLEncode
		String encodeURL = URLEncoder.encode(queryStr);
		//4.轉為全小寫
		String lowerEncodeURL = encodeURL.toLowerCase();

		//5.SHA256加密
		MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
		messageDigest.update(lowerEncodeURL.getBytes());
		byte byteBuffer[] = messageDigest.digest();
		// 將 byte 轉換爲 string
	    StringBuffer strHexString = new StringBuffer();
	    // 遍歷 byte buffer
	    for (int i = 0; i < byteBuffer.length; i++) {
	        String hex = Integer.toHexString(0xff & byteBuffer[i]);
	        if (hex.length() == 1) {
	            strHexString.append('0');
	        }
	            strHexString.append(hex);
	        }
	    //6.得到返回結果
	    String strResult = strHexString.toString();
	        
	    //7.再轉大寫產生CheckMacValue
	    String CheckMacValue = strResult.toUpperCase();
	    //8.放入session
	    m.addAttribute("CheckMacValue", CheckMacValue);
		    
	    
		m.addAttribute("shippingFee",shippingFee);
		m.addAttribute("totalWithDeliveryFee",totalWithDeliveryFee);
		
		//new一個order物件，開始放入一些屬性，先放到session，到最後資料齊了再存入SQL
		Order order = (Order) m.getAttribute("order");
		order.setOrderNo(orderNo);
		order.setAccount(SecurityContextHolder.getContext().getAuthentication().getName());
		order.setPayType(payType);
		order.setCommoditySubtotal(commoditySubtotal);
		
		//物流方式存到訂單
		if (shippingType.equals("UNIMART")) {
			order.setShippingType("7-ELEVEN超商取貨");
		}else if (shippingType.equals("HILIFE")) {
			order.setShippingType("萊爾富超商取貨");
		}else if (shippingType.equals("FAMI")) {
			order.setShippingType("全家超商取貨");
		}else {
			order.setShippingType(shippingType);
		}
		
		//且物流方式要放到session決定step3的畫面顯示
		m.addAttribute("shippingType", shippingType);
		
		
		order.setShippingFee(shippingFee);
		order.setOrderTotal(totalWithDeliveryFee);
		m.addAttribute("order", order);
		
		m.addAttribute("payType", payType);
		
		return "shopping/order_form";
		
	}
	
	
	//選完物流後從商店地圖導回
	@PostMapping(path = "/store_result")
	public String deliverStoreChoose( @RequestParam("CVSStoreID")String storeID, @RequestParam("CVSStoreName")String storeName, 
									@RequestParam("CVSAddress")String storeAddress, Model m){
		
		Order order = (Order) m.getAttribute("order");
		order.setStoreID(storeID);
		order.setStoreName(storeName);
		order.setDeliveryAddress(storeAddress);
		m.addAttribute("order", order);
		
		m.addAttribute("storeName", storeName);
		m.addAttribute("storeAddress", storeAddress);
		
		return "shopping/order_form";
	}
	

}
