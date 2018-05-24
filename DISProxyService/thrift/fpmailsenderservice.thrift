include "fpmailsender_response.thrift"

namespace java crdhn.fsp.thrift
namespace cpp CRDHN.FPlatform

service TFPMailSenderService {
	i32 sendMail(1: string recipient, 2: string subject, 3: string content);
}

