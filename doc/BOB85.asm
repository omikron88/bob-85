	org	00000H
;
RS0:
	lxi	sp,009E6H
	mvi	a,008H
	sim
	ei
	lxi	h,08085H
	call	ZOBR
	jmp	DAL1
;
RS2:
	xthl
	push	psw
	mov	a,m
	call	ZOBR
ZAS:
	call	ZNAK
	cpi	060H
	jnz	ZAS
	call	TMA
	pop	psw
	xthl
	ret
;
TRA:
	nop
	nop
	nop
	nop
RS5:
	nop
	nop
	nop
	nop
RS55:
	nop
	nop
	nop
	nop
RS6:
	nop
	nop
	nop
	nop
RS65:
	nop
	nop
	nop
	nop
RS7:
	nop
	nop
	nop
	ret
;
RS75:
	nop
	nop
	nop
DAL2:
	call	TMA
DAL1:
	call	POMLK
START:
	call	ZNAK
	cpi	010H
	jc	CHYBA
	jz	GO
	cpi	020H
	jz	SMEM
	cpi	040H
	jz	RS0
	cpi	050H
	jz	RS0
	jmp	MGF
;
GO:
	call	POM
	shld	X09FE
	push	psw
	mvi	a,0C3H
	lxi	d,L09FD
	stax	d
	call	TMA
	pop	psw
	jmp	L09FD
;
POM:
	call	ZOBR
	call	ADR
	call	ZNAK
TESTQ:
	cpi	060H
	jnz	CHYBA
	ret
;
ADR:
	push	psw
	call	TESTD
	call	ROT
	push	psw
	mov	a,h
	call	POSUV
	mov	h,a
	pop	psw
	add	h
	mov	h,a
	pop	psw
	call	ZOBR
	push	psw
	call	TESTD
	push	psw
	mov	a,h
	call	ROT
	call	POSUV
	call	ROT
	mov	h,a
	pop	psw
	add	h
	mov	h,a
	pop	psw
	call	ZOBR
	push	psw
	call	TESTD
	call	ROT
	push	psw
	mov	a,l
	call	POSUV
	mov	l,a
	pop	psw
	add	l
	mov	l,a
	pop	psw
	call	ZOBR
	push	psw
	call	TESTD
	push	psw
	mov	a,l
	call	ROT
	call	L010E
	call	ROT
	mov	l,a
	pop	psw
	add	l
	mov	l,a
	pop	psw
	call	ZOBR
	jmp	ADR
;
TESTD:
	call	ZNAK
	cpi	010H
	rc
	inx	sp
	inx	sp
	inx	sp
	inx	sp
	ora	e
	inx	sp
	jmp	TESTQ
;
POMLK:
	lxi	h,RS0
	push	psw
	mvi	a,004H
	out	00EH
	out	00FH
	pop	psw
	ret
;
TMA:
	push	psw
	xra	a
	out	00AH
	out	00BH
	out	00CH
	out	00DH
	out	00EH
	out	00FH
	pop	psw
	ret
;
SMEM:
	call	POM
PAMET:
	mov	a,m
  call ZOBR
  call ZNAK
	cpi 010H
	jc	DATA1
TESTSR:
	cpi	060H
	jnz	ZPET
	inx	h
	jmp	PAMET
;
ZPET:
	cpi	030H
	jnz	CHYBA
	dcx	h
	jmp	PAMET
;
DATA1:
	call	ROT
	push	psw
	mov	a,m
	call	POSUV
	mov	m,a
	pop	psw
	add	m
	call	ZOBR
	push	psw
	mov	b,a
	mov	m,a
	call	ZNAK
	cpi	010H
	jc	DATA2
	pop	b
	jmp	TESTSR
;
DATA2:
	mov	m,a
	mov	a,b
	call	ROT
	call	POSUV
	call	ROT
	add	m
	mov	m,a
	pop	b
	jmp	PAMET
;
CHYBA:
	lxi	h,0EEEEH
	call	ZOBR
	jmp	DAL1
;
ROT:
	rlc
	rlc
	rlc
	rlc
	ret
;
	nop
ZNAK:
	push	b
	call	JEDEN
	mov	c,a
	call	MS3
VSTUP1:
	in	00AH
	ral
	stc
	cmc
	rar
	cmp	c
	pop	b
	rz
	push	b
	mov	c,a
	jmp	VSTUP1
;
JEDEN:
	in	00AH
	ral
	jnc	JEDEN
	cmc
	rar
	ret
;
MS3:
	push	d
	push	psw
	lxi	d,0019DH
DC:
	dcx	d
	mov	a,d
	adi	000H
	jnz	DC
	pop	psw
	pop	d
	ret
;
ZOBR:
	push	psw
	push	d
	push	h
	call	KODNUL
	out	00FH
	mov	a,d
	call	ROT
	call	KODNUL
	out	00EH
	pop	h
	push	h
	mov	a,h
	call	KODNUL
	out	00BH
	mov	a,d
	call	ROT
	call	KODNUL
	out	00AH
	pop	h
	push	h
	mov	a,l
	call	KODNUL
	out	00DH
	mov	a,d
	call	ROT
	call	KODNUL
	out	00CH
	pop	h
	pop	d
	pop	psw
	ret
;
	nop
	nop
POSUV:
	mov	d,a
	mvi	c,004H
CYKL:
	stc
	cmc
	ral
	dcr	c
	jnz	CYKL
	rrc
	rrc
	rrc
	rrc
	ret
;
KODNUL:
	call	POSUV
	call	KOD
	ret
;
KOD:
	push	h
	lxi	h,TABK
	add	l
	mov	l,a
	mov	a,m
	pop	h
	ret
;
TABK:
	db	0F3H
	db	060H
	db	0B5H
	db	0F4H
	db	066H
	db	0D6H
	db	0D7H
	db	070H
	db	0F7H
	db	076H
	db	077H
	db	0C7H
	db	093H
	db	0E5H
	db	097H
	db	017H
;
	nop
MGF:
	cpi	030H
	jnz	CHYBA
	call	POMLK
	mvi	a,005H
	out	00FH
MGF1:
	call	ZNAK
	cpi	050H
	jz	PM
	cpi	040H
	jnz	MGF1
MP:
	call	UVOD
	push	h
	call	KS
	nop
	mov	m,a
	pop	h
	inr	b
MP1:
	mvi	c,0FAH
	mvi	a,0C0H
MP2:
	call	OBDEL
	dcr	c
	jnz	MP2
	xra	a
	call	OBDEL
MP3:
	mov	c,m
	dcr	c
	dcx	sp
	stax	b
	inx	h
	dcr	b
	stax	b
	lxi	sp,0C702H
TAPEO:
	di
	push	d
	push	b
	mvi	b,009H
TO1:
	xra	a
	mvi	a,0C0H
	call	OBDEL
	mov	a,c
	rar
	mov	c,a
	mvi	a,001H
	rar
	rar
	call	OBDEL
	xra	a
	call	OBDEL
	dcr	b
	jnz	TO1
	pop	b
	pop	d
	ei
	ret
;
OBDEL:
	mvi	d,010H
OB1:
	sim
	mvi	e,01EH
OB2:
	dcr	e
	jnz	OB2
	xri	080H
	dcr	d
	jnz	OB1
	ret
;
PM:
	call	UVOD
	inr	b
	push	h
	push	b
PM1:
	mvi	c,0FAH
PM2:
	call	VSTUP2
	jnc	PM1
	dcr	c
	jnz	PM2
PM3:
	push	b
	call	TAPEIN
	mov	m,c
	inx	h
	pop	b
	dcr	b
	jnz	PM3
	pop	b
	pop	h
	dcr	b
	call	KS
	cmp	m
	jnz	CHYBA
	rst	0
TAPEIN:
	mvi	b,009H
TI1:
	mvi	d,016H
TI2:
	dcr	d
	call	VSTUP2
	jc	TI2
	call	VSTUP2
	jc	TI2
TI3:
	inr	d
	call	VSTUP2
	jnc	TI3
	call	VSTUP2
	jnc	TI3
	mov	a,d
	ral
	mov	a,c
	rar
	mov	c,a
	dcr	b
	jnz	TI1
	ret
;
VSTUP2:
	mvi	e,016H
L02BE:
	dcr	e
	jnz	L02BE
	rim
	ral
	ret
;
UVOD:
	call	POM
	xra	a
	call	POCETB
	call	TMA
	ret
;
POCETB:
	call	TESTZN
	call	ROT
	mov	b,a
	call	TESTZN
	add	b
	mov	b,a
	jmp	POCETB
;
TESTZN:
	call	ZOBR
	call	ZNAK
	cpi	060H
	pop	d
	rz
	push	d
	cpi	010H
	jnc	CHYBA
	ret
;
KS:
	push	b
	xra	a
	add	h
	add	l
	add	b
KS1:
	add	m
	inx	h
	dcr	b
	jnz	KS1
	cma
	inr	a
	pop	b
	ret
;
	nop
