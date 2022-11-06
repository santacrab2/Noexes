grammar Code ;

@header {
import me.mdbell.noexs.code.model.*;
}

options {
	caseInsensitive = true ;
}

codes	returns [Codes cs]

@init {        $cs= new Codes();  }
									:  (c = code {$cs.addCode($c.c);})+  EOF;



code 	returns [Code c]

@init {        $c= new Code();  }			
									:  label=STRING {$c.setLabelWithQuotes($label.text);} 
									           	(v = writeValue {$c.setWriteValue($v.wv);} | b =expr {$c.setBlock($b.b);})  ;

/** 
 * Blocks
 */
 
expr		returns [Block b]     			: b1 = block {$b=$b1.b;}| c= cond_block {$b=$c.cb;};

cond_block 	returns [ConditionalBlock cb] 	: IF_BUTTON key = KEYPAD b1 = block {$cb = new ConditionalBlock(new ConditionPressButton(Keypad.getKeypad($key.text)), $b1.b);} (ELSE eb= block {$cb.setElseBlock($eb.b);})? ;

block	returns [Block b]

@init {        $b= new Block();  }
											:  '{'  (wv= writeValue ';'{$b.addInstruction($wv.wv);})+ '}'	;

/** 
 * Instructions
 */
 
 																																																						
writeValue returns [WriteValue wv]	:  p = pointer '=' '(' vt = VALUETYPE ')' v = (VALUE|FLOAT_VALUE)	{$wv= new WriteValue($p.pt, EValueType.getValueType($vt.text),$v.text);};
pointer returns [Pointer pt]		:   	'[' recPtWOff = pointer ']' symb=('+'|'-') ptOff = VALUE 	{$pt= new Pointer($recPtWOff.pt,EArithmeticOperation.getArithmeticOperationFromSymbol($symb.text),$ptOff.text);}
										| 	'[' recPt = pointer ']' 									{$pt= new Pointer($recPt.pt);}
		 								|  	addrT = ADDRTYPE '+' off = VALUE 							{$pt= new Pointer(ECodeMemoryRegion.getMemoryRegion($addrT.text),$off.text);};

/** 
 * Litterals
 */
VALUETYPE	: 'U8' | 'S8' | 'U16' | 'S16' | 'U32' | 'S32' | 'U64' | 'S64' | 'FLT' | 'DBL' | 'PTR' ;
ADDRTYPE	: 'main' | 'heap' | 'alias' | 'aslr' ;
VALUE		: ('0x' )?[0-9A-F]+ ;
FLOAT_VALUE	: [0-9]+ '.' [0-9]+ ;
STRING	: '"' (ESC | ~ ('\\' |'"' )	)* '"' ;
ESC : '\\' ( 'n' | 'r' ) ;

KEYPAD
	: 'A'
	| 'B'
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

IF_BUTTON	: 'ifBut' ;
ELSE		: 'else'  ;

WS	: [ \t\r\n]+ -> skip ;
