grammar Code ;

@header {
import me.mdbell.noexs.code.model.*;
}

options {
	caseInsensitive = true ;
}
code 	returns [Code c]			:  v = writeValue  EOF 												{$c= new Code($v.wv);} ;
writeValue returns [WriteValue wv]	:  p = pointer '=' '(' vt = VALUETYPE ')' v = VALUE					{$wv= new WriteValue($p.pt, ValueType.getValueType($vt.getText()),$v.getText());};
pointer returns [Pointer pt]		:   	'[' recPtWOff = pointer ']' symb=('+'|'-') ptOff = VALUE 	{$pt= new Pointer($recPtWOff.pt,ArithmeticOperation.getArithmeticOperationFromSymbol($symb.getText()),$ptOff.getText());}
										| 	'[' recPt = pointer ']' 									{$pt= new Pointer($recPt.pt);}
		 								|  	addrT = ADDRTYPE '+' off = VALUE 							{$pt= new Pointer(MemoryRegion.getMemoryRegion($addrT.getText()),$off.getText());};
VALUETYPE	: 'U8' | 'S8' | 'U16' | 'S16' | 'U32' | 'S32' | 'U64' | 'S64' | 'FLT' | 'DBL' | 'PTR' ;
ADDRTYPE	: 'main' | 'heap' | 'alias' | 'aslr' ;
VALUE		: ('0x' )?[0-9A-F]+ ;

KEYPAD
	: 'A' | 'B'
	| 'X'
	| 'Y'
	| 'LEFT_STICK_PRESSED'
	| 'RIGHT_STICK_PRESSED'
	| 'L'
	| 'R'
	| 'ZL'
	| 'ZR'
	| 'PLUS'
	| 'MINUS'
	| 'LEFT'
	| 'UP'
	| 'RIGHT'
	| 'DOWN'
	| 'LEFT_STICK_LEFT'
	| 'LEFT_STICK_UP'
	| 'LEFT_STICK_RIGHT'
	| 'LEFT_STICK_DOWN'
	| 'RIGHT_STICK_LEFT'
	| 'RIGHT_STICK_UP'
	| 'RIGHT_STICK_RIGHT'
	| 'RIGHT_STICK_DOWN'
	| 'SL'
	| 'SR'
	;

WS	: [ \t\r\n]+ -> skip ;
