/*
 * metacore.h
 *
 *  Created on: Dec 23, 2015
 *      Author: jwna@wisecube.com
 */

#ifndef WSDEF_H_
#define WSDEF_H_

#ifdef __cplusplus
extern "C" {
#endif

typedef void* WSHANDLE;
typedef void  (*meta_notify_cb)(WSHANDLE handle, int event);

#define MAX_RESPONSE	1024

#define E_ERR_PICKUP			1
#define E_ERR_JAM_FEED			2
#define E_ERR_JAM_MICR			3
#define E_ERR_JAM_SCAN			4
#define E_ERR_JAM_ESCROW		5
#define E_ERR_JAM_REJECT		6
#define E_ERR_JAM_PRINT			7
#define E_ERR_JAM_STAMP			8
#define E_ERR_JAM_STACK			9

#define E_ERR_SETUP_ARG			10
#define E_ERR_ESCR_OVER			11
#define E_ERR_NO_INLET			12
#define E_ERR_NO_ESCROW			13
#define E_ERR_ALIGN				14
#define E_ERR_EPICKUP			15
#define E_ERR_MAX_ESCROW		16
#define E_ERR_NO_FEED			17
#define E_ERR_NO_CHK			18
#define E_ERR_JAM_CHK			19
#define E_ERR_JAM_EXIT			20
#define E_ERR_JAM_DOUBLE		21
#define E_ERR_JAM_FEEDBACK		22
#define E_ERR_SLIP				23 //0x17
#define E_ERR_DOCTIMEOUT		30
#define E_ERR_INS_TAKEN			32

#define E_ERR_INIT				50
#define E_ERR_EEP_WR			51
#define E_ERR_EEP_RD			52
#define E_ERR_DOWNLOAD			53
#define E_ERR_UNDEFCMD			54		// UNDEFINDED command
#define E_ERR_BROKENFRM			55		// broken frame, broken header
#define	E_ERR_CMD_ARG           57		// Illegal argument in command

#define E_ERR_USB				70		/** Input/output error */
#define E_ERR_IO				71		/** Input/output error */
#define E_ERR_INVALID_PARAM		72		/** Invalid parameter */
#define E_ERR_ACCESS			73		/** Access denied (insufficient permissions) */
#define E_ERR_NO_DEVICE			74		/** No such device (it may have been disconnected) */
#define E_ERR_NOT_FOUND			75		/** Entity not found */
#define E_ERR_BUSY				76		/** Resource busy */
#define E_ERR_TIMEOUT			77		/** Operation timed out */
#define E_ERR_OVERFLOW			78		/** Overflow */
#define E_ERR_PIPE				79		/** Pipe error */
#define E_ERR_INTERRUPTED		80		/** System call interrupted (perhaps due to signal) */
#define E_ERR_NO_MEM			81		/** Insufficient memory */
#define E_ERR_NOT_SUPPORTED		82		/** Operation not supported or unimplemented on this platform */
#define E_ERR_NOT_INIT			83		/** No Initialize data */
#define E_ERR_OUTOFMEMORY		84		/** memory alloc error */

#define E_ERR_SYNTAX			91		/** Syntax Error */
#define E_ERR_NOTSUPP_CMD		92		/** not supported command */
#define E_ERR_NOCONNECT			93
#define	E_ERR_OCRENCODING		94		// no ocr encoding
#define	E_ERR_OCRENGINE			95		// no ocr engine
#define	E_ERR_NORESPONSE		96		// no response
#define	E_ERR_FILE_IO			97		// no response
#define	E_ERR_NOT_IMAGEDATA		98		// nothing image data
#define	E_ERR_ETC				99		// undefine error

#define WS_DEVICE_ANY			0x0
#define WS_DEVICE_ISM			0xecec1003
#define WS_DEVICE_ID_150		0xecec1003
#define WS_DEVICE_ISV			0x1c511203
#define WS_DEVICE_ID_1501		0x1c511010

#define WS_DEVICE_LT			0x1c511102
#define WS_DEVICE_VS			0x1c511010
#define WS_DEVICE_YS			0x1c511000
#define WS_DEVICE_CS			0x1c510120

WSHANDLE wsOpen( unsigned int devicetype, meta_notify_cb cb_notify );
void wsClose(WSHANDLE handle);
int wsWrite(WSHANDLE handle, void* data, size_t length );
size_t wsRead(WSHANDLE handle, void* data, size_t *length );

#ifdef __cplusplus
}
#endif

#endif /* WSDEF_H_ */ 
