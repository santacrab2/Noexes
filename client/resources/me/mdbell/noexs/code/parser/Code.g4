grammar Code ;

@header {
import me.mdbell.noexs.code.model.*;
}

options {
	caseInsensitive = true ;
}
code 	returns [Code c]			:  v = writeValue  EOF 												{$c= new Code($v.wv);} ;
writeValue returns [WriteValue wv]	:  p = pointer '=' '(' vt = VALUETYPE ')' v = VALUE					{$wv= new WriteValue($p.pt, ValueType.getValueType($vt.getText()),$v.getText());};
pointer returns [Pointer pt]		:   	'[' recPtWOff = pointer ']' symb=('+'|'-') ptOff = VALUE 	{$pt= new Pointer($recPtWOff.pt,ArithmeticType.getArithmeticTypeFromSymbol($symb.getText()),$ptOff.getText());}
										| 	'[' recPt = pointer ']' 									{$pt= new Pointer($recPt.pt);}
		 								|  	addrT = ADDRTYPE '+' off = VALUE 							{$pt= new Pointer(AddressType.getAddressType($addrT.getText()),$off.getText());};
VALUETYPE	: 'U8' | 'S8' | 'U16' | 'S16' | 'U32' | 'S32' | 'U64' | 'S64' | 'FLT' | 'DBL' | 'PTR' ;
ADDRTYPE	: 'main' | 'heap' ;
VALUE		: ('0x' )?[0-9A-F]+ ;

WS	: [ \t\r\n]+ -> skip ;
