package com.tulasimultispecialityhospital.connections

import android.content.Context
import com.google.gson.GsonBuilder
import com.tulasimultispecialityhospital.models.*
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ConnectionManager private constructor(context: Context) {

    private val firstTimeInstanceForPicasso = true

    //live


    companion object {
        val SERVICE_ENDPOINT = "http://mysmartbusiness.co.in/services/"
        private var retrofit: Retrofit? = null
        private var connectionManger: ConnectionManager? = null
        private var connectionAPI: ConnectionAPI? = null
        fun getInstance(ctx: Context): ConnectionManager {
            if (connectionManger == null) {
                try {
                    val client = UnsafeOkHttpClient().getUnsafeOkHttpClient()

                    val gson = GsonBuilder()
                        .setLenient()
                        .create()
                    try {
                        retrofit = Retrofit.Builder()
                            .baseUrl(SERVICE_ENDPOINT)
                            .addConverterFactory(GsonConverterFactory.create(gson))
                            .client(client)
                            .build()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    connectionAPI = retrofit!!.create(ConnectionAPI::class.java)

                } catch (e: Exception) {
                    e.printStackTrace()
                }

                connectionManger = ConnectionManager(ctx)
            }
            return connectionManger as ConnectionManager
        }

    }


//    private fun ConnectionManager(context: Context) {
//        try {
//            val client = UnsafeOkHttpClient().getUnsafeOkHttpClient()
//
//            val gson = GsonBuilder()
//                .setLenient()
//                .create()
//            try {
//                retrofit = Retrofit.Builder()
//                    .baseUrl(SERVICE_ENDPOINT)
//                    .addConverterFactory(GsonConverterFactory.create(gson))
//                    .client(client)
//                    .build()
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//
//            connectionAPI = retrofit!!.create(ConnectionAPI::class.java)
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
//    }
//
//
//    fun getInstance(context: Context): ConnectionManager {
//
//        if (connectionManger == null) {
//            connectionManger = ConnectionManager(context) as ConnectionManager
//        }
//        //else {
//        //            connectionManger = new ConnectionManager(context);
//        //        }
//        return connectionManger as ConnectionManager
//    }

    interface LoginResponseListener {
        fun onLoginResponseSuccess(response: Response<UserModel>?)

        fun onLoginResponse(response: UserModel?)

        fun onLoginResponseFailure(message: String?)
    }

    interface DoctorProfileResponseListener {
        fun onDoctorProfileResponseSuccess(response: Response<DoctorModel>?)

        fun onDoctorProfileResponse(response: DoctorModel?)

        fun onDoctorProfileResponseFailure(message: String?)
    }

    interface AppointmentTimeSlotsResponseListener {
        fun onAppointmentTimeSlotsResponseSuccess(response: Response<TimeSlotModel>?)

        fun onAppointmentTimeSlotsResponse(response: TimeSlotModel?)

        fun onAppointmentTimeSlotsResponseFailure(message: String?)
    }

    interface AppointmentResponseListener {
        fun onAppointmentResponseSuccess(response: Response<AppointmentModel>?)

        fun onAppointmentResponse(response: AppointmentModel?)

        fun onAppointmentResponseFailure(message: String?)
    }

    interface ImageGalleryResponseListener {
        fun onImageGalleryResponseSuccess(response: Response<AlbumModel>)

        fun onImageGalleryResponse(response: AlbumModel?)

        fun onImageGalleryResponseFailure(message: String)
    }
    interface TestimonialsResponseListener {
        fun onTestimonialsResponseSuccess(response: Response<TestimonialModel>)

        fun onTestimonialsResponse(response: TestimonialModel?)

        fun onTestimonialsResponseFailure(message: String)
    }

    interface FCMTokenResponseListener {
        fun onFCMTokenResponseSuccess(response: Response<UserModel>)

        fun onFCMTokenResponse(response: UserModel?)

        fun onFCMTokenResponseFailure(message: String)
    }



    private fun getErrorMessage(t: Throwable): String {
        val errorMessage: String/*Your connectivity seems very low. Please make sure you have good connection.*/
        if (t is SocketTimeoutException) {
            errorMessage = "Your connectivity seems very low. Please make sure you have good connection."
            //errorDesc = String.valueOf(error.getThrowable().getCause());
        } else if (t is IOException) {
            errorMessage = "Your request was unsuccessful. Please try again"
            //errorDesc = String.valueOf(error.getThrowable().getCause());
        } else if (t is HttpException) {
            errorMessage = "Your request was unsuccessful. Please try again"
            //errorDesc = String.valueOf(error.getThrowable().getCause());
        } else if (t is UnknownHostException) {
            errorMessage = "Your request was unsuccessful. Please try again"
            //errorDesc = String.valueOf(error.getThrowable().getCause());
        } else if (t is IllegalStateException) {
            errorMessage = "Your request was unsuccessful. Please try again"
            //errorDesc = String.valueOf(error.getThrowable().getCause());
        } else {
            errorMessage = AssertionError("Unknown error kind: " + t.localizedMessage).toString()
            //errorDesc = String.valueOf(error.getThrowable().getLocalizedMessage());
        }

        return errorMessage
    }

    fun getLoginData(generalRequest: GeneralRequest, loginResponseListener: LoginResponseListener) {
        connectionAPI?.getLoginData(generalRequest.mobile, generalRequest.password, generalRequest.doctor_id)
            ?.enqueue(object :
                Callback<UserModel> {
                override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                    if (response.code() == 500 || response.code() == 400) {
                        val converter =
                            retrofit?.responseBodyConverter<UserModel>(
                                UserModel::class.java!!,
                                arrayOfNulls<Annotation>(0)
                            )
                        try {
                            val errorResponse = converter?.convert(response.errorBody()!!)
                            loginResponseListener.onLoginResponse(errorResponse)
                        } catch (e: IOException) {
                            loginResponseListener.onLoginResponseFailure("Something went wrong. Try again")
                            e.printStackTrace()
                        }

                    } else if (response.code() == 200)
                        loginResponseListener.onLoginResponseSuccess(response)
                    else
                        loginResponseListener.onLoginResponseFailure("Something went wrong. Try again")
                }

                override fun onFailure(call: Call<UserModel>, t: Throwable) {
                    t.printStackTrace()
                    loginResponseListener.onLoginResponseFailure(getErrorMessage(t))
                }
            })
    }

    fun getDoctorProfileData(
        generalRequest: GeneralRequest,
        doctorProfileResponseListener: DoctorProfileResponseListener
    ) {
        connectionAPI?.getDoctorProfile(generalRequest.doctor_id)?.enqueue(object :
            Callback<DoctorModel> {
            override fun onResponse(call: Call<DoctorModel>, response: Response<DoctorModel>) {
                if (response.code() == 500 || response.code() == 400) {
                    val converter =
                        retrofit?.responseBodyConverter<DoctorModel>(
                            UserModel::class.java!!,
                            arrayOfNulls<Annotation>(0)
                        )
                    try {
                        val errorResponse = converter?.convert(response.errorBody()!!)
                        doctorProfileResponseListener.onDoctorProfileResponse(errorResponse)
                    } catch (e: IOException) {
                        doctorProfileResponseListener.onDoctorProfileResponseFailure("Something went wrong. Try again")
                        e.printStackTrace()
                    }

                } else if (response.code() == 200)
                    doctorProfileResponseListener.onDoctorProfileResponseSuccess(response)
                else
                    doctorProfileResponseListener.onDoctorProfileResponseFailure("Something went wrong. Try again")
            }

            override fun onFailure(call: Call<DoctorModel>, t: Throwable) {
                t.printStackTrace()
                doctorProfileResponseListener.onDoctorProfileResponseFailure(getErrorMessage(t))
            }
        })
    }

    fun postRegistrationData(generalRequest: GeneralRequest, loginResponseListener: LoginResponseListener) {
        connectionAPI?.postRegistrationData(
            generalRequest.user_fullname,
            generalRequest.user_email,
            generalRequest.user_mobile,
            generalRequest.user_pwd,
            generalRequest.user_gender,
            generalRequest.user_dob,
            generalRequest.user_city,
            generalRequest.doctor_id
        )?.enqueue(object :
            Callback<UserModel> {
            override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                if (response.code() == 500 || response.code() == 400) {
                    val converter =
                        retrofit?.responseBodyConverter<UserModel>(UserModel::class.java!!, arrayOfNulls<Annotation>(0))
                    try {
                        val errorResponse = converter?.convert(response.errorBody()!!)
                        loginResponseListener.onLoginResponse(errorResponse)
                    } catch (e: IOException) {
                        loginResponseListener.onLoginResponseFailure("Something went wrong. Try again")
                        e.printStackTrace()
                    }

                } else if (response.code() == 200)
                    loginResponseListener.onLoginResponseSuccess(response)
                else
                    loginResponseListener.onLoginResponseFailure("Something went wrong. Try again")
            }

            override fun onFailure(call: Call<UserModel>, t: Throwable) {
                t.printStackTrace()
                loginResponseListener.onLoginResponseFailure(getErrorMessage(t))
            }
        })
    }


    fun getDoctorClinicsData(
        generalRequest: GeneralRequest,
        doctorProfileResponseListener: DoctorProfileResponseListener
    ) {
        connectionAPI?.getDoctorClinics(generalRequest.doctor_id)?.enqueue(object :
            Callback<DoctorModel> {
            override fun onResponse(call: Call<DoctorModel>, response: Response<DoctorModel>) {
                if (response.code() == 500 || response.code() == 400) {
                    val converter =
                        retrofit?.responseBodyConverter<DoctorModel>(
                            UserModel::class.java!!,
                            arrayOfNulls<Annotation>(0)
                        )
                    try {
                        val errorResponse = converter?.convert(response.errorBody()!!)
                        doctorProfileResponseListener.onDoctorProfileResponse(errorResponse)
                    } catch (e: IOException) {
                        doctorProfileResponseListener.onDoctorProfileResponseFailure("Something went wrong. Try again")
                        e.printStackTrace()
                    }

                } else if (response.code() == 200)
                    doctorProfileResponseListener.onDoctorProfileResponseSuccess(response)
                else
                    doctorProfileResponseListener.onDoctorProfileResponseFailure("Something went wrong. Try again")
            }

            override fun onFailure(call: Call<DoctorModel>, t: Throwable) {
                t.printStackTrace()
                doctorProfileResponseListener.onDoctorProfileResponseFailure(getErrorMessage(t))
            }
        })
    }

    fun getDoctorAboutData(
        generalRequest: GeneralRequest,
        doctorProfileResponseListener: DoctorProfileResponseListener
    ) {
        connectionAPI?.getDoctorAbout(generalRequest.doctor_id)?.enqueue(object :
            Callback<DoctorModel> {
            override fun onResponse(call: Call<DoctorModel>, response: Response<DoctorModel>) {
                if (response.code() == 500 || response.code() == 400) {
                    val converter =
                        retrofit?.responseBodyConverter<DoctorModel>(
                            UserModel::class.java!!,
                            arrayOfNulls<Annotation>(0)
                        )
                    try {
                        val errorResponse = converter?.convert(response.errorBody()!!)
                        doctorProfileResponseListener.onDoctorProfileResponse(errorResponse)
                    } catch (e: IOException) {
                        doctorProfileResponseListener.onDoctorProfileResponseFailure("Something went wrong. Try again")
                        e.printStackTrace()
                    }

                } else if (response.code() == 200)
                    doctorProfileResponseListener.onDoctorProfileResponseSuccess(response)
                else
                    doctorProfileResponseListener.onDoctorProfileResponseFailure("Something went wrong. Try again")
            }

            override fun onFailure(call: Call<DoctorModel>, t: Throwable) {
                t.printStackTrace()
                doctorProfileResponseListener.onDoctorProfileResponseFailure(getErrorMessage(t))
            }
        })
    }

    fun getAppointmentTimeSlotsData(
        generalRequest: GeneralRequest,
        appointmentTimeSlotsResponseListener: AppointmentTimeSlotsResponseListener
    ) {
        connectionAPI?.getAppointmentSlots(
            generalRequest.doctor_id,
            generalRequest.clinic_id,
            generalRequest.slot_date,
            generalRequest.slot_type
        )?.enqueue(object : Callback<TimeSlotModel> {
            override fun onResponse(call: Call<TimeSlotModel>, response: Response<TimeSlotModel>) {
                if (response.code() == 500 || response.code() == 400) {
                    val converter =
                        retrofit?.responseBodyConverter<TimeSlotModel>(
                            UserModel::class.java!!,
                            arrayOfNulls<Annotation>(0)
                        )
                    try {
                        val errorResponse = converter?.convert(response.errorBody()!!)
                        appointmentTimeSlotsResponseListener.onAppointmentTimeSlotsResponse(errorResponse)
                    } catch (e: IOException) {
                        appointmentTimeSlotsResponseListener.onAppointmentTimeSlotsResponseFailure("Something went wrong. Try again")
                        e.printStackTrace()
                    }

                } else if (response.code() == 200)
                    appointmentTimeSlotsResponseListener.onAppointmentTimeSlotsResponseSuccess(response)
                else
                    appointmentTimeSlotsResponseListener.onAppointmentTimeSlotsResponseFailure("Something went wrong. Try again")
            }

            override fun onFailure(call: Call<TimeSlotModel>, t: Throwable) {
                t.printStackTrace()
                appointmentTimeSlotsResponseListener.onAppointmentTimeSlotsResponseFailure(getErrorMessage(t))
            }
        })
    }

    fun bookAppointmentData(
        generalRequest: GeneralRequest,
        appointmentResponseListener: AppointmentResponseListener
    ) {
        connectionAPI?.bookAppointment(
            generalRequest.doctor_id,
            generalRequest.clinic_id,
            generalRequest.slot_date,
            generalRequest.slot_type,
            generalRequest.slot_timing,
            generalRequest.appointment_type,
            generalRequest.user_id
        )?.enqueue(object : Callback<AppointmentModel> {
            override fun onResponse(call: Call<AppointmentModel>, response: Response<AppointmentModel>) {
                if (response.code() == 500 || response.code() == 400) {
                    val converter =
                        retrofit?.responseBodyConverter<AppointmentModel>(
                            UserModel::class.java!!,
                            arrayOfNulls<Annotation>(0)
                        )
                    try {
                        val errorResponse = converter?.convert(response.errorBody()!!)
                        appointmentResponseListener.onAppointmentResponse(errorResponse)
                    } catch (e: IOException) {
                        appointmentResponseListener.onAppointmentResponseFailure("Something went wrong. Try again")
                        e.printStackTrace()
                    }

                } else if (response.code() == 200)
                    appointmentResponseListener.onAppointmentResponseSuccess(response)
                else
                    appointmentResponseListener.onAppointmentResponseFailure("Something went wrong. Try again")
            }

            override fun onFailure(call: Call<AppointmentModel>, t: Throwable) {
                t.printStackTrace()
                appointmentResponseListener.onAppointmentResponseFailure(getErrorMessage(t))
            }
        })
    }

    fun getUserAppointmentsData(
        generalRequest: GeneralRequest,
        appointmentResponseListener: AppointmentResponseListener
    ) {
        connectionAPI?.userAppointments(
            generalRequest.doctor_id,
            generalRequest.user_id
        )?.enqueue(object : Callback<AppointmentModel> {
            override fun onResponse(call: Call<AppointmentModel>, response: Response<AppointmentModel>) {
                if (response.code() == 500 || response.code() == 400) {
                    val converter =
                        retrofit?.responseBodyConverter<AppointmentModel>(
                            UserModel::class.java!!,
                            arrayOfNulls<Annotation>(0)
                        )
                    try {
                        val errorResponse = converter?.convert(response.errorBody()!!)
                        appointmentResponseListener.onAppointmentResponse(errorResponse)
                    } catch (e: IOException) {
                        appointmentResponseListener.onAppointmentResponseFailure("Something went wrong. Try again")
                        e.printStackTrace()
                    }

                } else if (response.code() == 200)
                    appointmentResponseListener.onAppointmentResponseSuccess(response)
                else
                    appointmentResponseListener.onAppointmentResponseFailure("Something went wrong. Try again")
            }

            override fun onFailure(call: Call<AppointmentModel>, t: Throwable) {
                t.printStackTrace()
                appointmentResponseListener.onAppointmentResponseFailure(getErrorMessage(t))
            }
        })
    }

    fun postForgotPasswordData(
        generalRequest: GeneralRequest,
        doctorProfileResponseListener: DoctorProfileResponseListener
    ) {
        connectionAPI?.getForgotPasswordData(generalRequest.user_mobile, generalRequest.doctor_id)?.enqueue(object :
            Callback<DoctorModel> {
            override fun onResponse(call: Call<DoctorModel>, response: Response<DoctorModel>) {
                if (response.code() == 500 || response.code() == 400) {
                    val converter =
                        retrofit?.responseBodyConverter<DoctorModel>(
                            UserModel::class.java!!,
                            arrayOfNulls<Annotation>(0)
                        )
                    try {
                        val errorResponse = converter?.convert(response.errorBody()!!)
                        doctorProfileResponseListener.onDoctorProfileResponse(errorResponse)
                    } catch (e: IOException) {
                        doctorProfileResponseListener.onDoctorProfileResponseFailure("Something went wrong. Try again")
                        e.printStackTrace()
                    }

                } else if (response.code() == 200)
                    doctorProfileResponseListener.onDoctorProfileResponseSuccess(response)
                else
                    doctorProfileResponseListener.onDoctorProfileResponseFailure("Something went wrong. Try again")
            }

            override fun onFailure(call: Call<DoctorModel>, t: Throwable) {
                t.printStackTrace()
                doctorProfileResponseListener.onDoctorProfileResponseFailure(getErrorMessage(t))
            }
        })
    }


    fun getImagesGallery(generalRequest: GeneralRequest, imageGalleryResponseListener: ImageGalleryResponseListener) {
        connectionAPI?.getImagesGalleryData(generalRequest.doctor_id)?.enqueue(object :
            Callback<AlbumModel> {
            override fun onResponse(call: Call<AlbumModel>, response: Response<AlbumModel>) {
                if (response.code() == 500 || response.code() == 400) {
                    val converter =
                        retrofit?.responseBodyConverter<AlbumModel>(
                            AlbumModel::class.java!!,
                            arrayOfNulls<Annotation>(0)
                        )
                    try {
                        val errorResponse = converter?.convert(response.errorBody()!!)
                        imageGalleryResponseListener.onImageGalleryResponse(errorResponse)
                    } catch (e: IOException) {
                        imageGalleryResponseListener.onImageGalleryResponseFailure("Something went wrong. Try again")
                        e.printStackTrace()
                    }

                } else if (response.code() == 200)
                    imageGalleryResponseListener.onImageGalleryResponseSuccess(response)
                else
                    imageGalleryResponseListener.onImageGalleryResponseFailure("Something went wrong. Try again")
            }

            override fun onFailure(call: Call<AlbumModel>, t: Throwable) {
                t.printStackTrace()
                imageGalleryResponseListener.onImageGalleryResponseFailure(getErrorMessage(t))
            }
        })
    }

    fun getTestimonials(generalRequest: GeneralRequest, testimonialsResponseListener: TestimonialsResponseListener) {
        connectionAPI?.getTestimonialsData(generalRequest.doctor_id)?.enqueue(object :
            Callback<TestimonialModel> {
            override fun onResponse(call: Call<TestimonialModel>, response: Response<TestimonialModel>) {
                if (response.code() == 500 || response.code() == 400) {
                    val converter =
                        retrofit?.responseBodyConverter<TestimonialModel>(
                            TestimonialModel::class.java!!,
                            arrayOfNulls<Annotation>(0)
                        )
                    try {
                        val errorResponse = converter?.convert(response.errorBody()!!)
                        testimonialsResponseListener.onTestimonialsResponse(errorResponse)
                    } catch (e: IOException) {
                        testimonialsResponseListener.onTestimonialsResponseFailure("Something went wrong. Try again")
                        e.printStackTrace()
                    }

                } else if (response.code() == 200)
                    testimonialsResponseListener.onTestimonialsResponseSuccess(response)
                else
                    testimonialsResponseListener.onTestimonialsResponseFailure("Something went wrong. Try again")
            }

            override fun onFailure(call: Call<TestimonialModel>, t: Throwable) {
                t.printStackTrace()
                testimonialsResponseListener.onTestimonialsResponseFailure(getErrorMessage(t))
            }
        })
    }

    fun postFCMTokenToServer(generalRequest: GeneralRequest, fcmTokenResponseListener: FCMTokenResponseListener) {
        connectionAPI?.postDeviceId(generalRequest.mobile!!,generalRequest.doctor_id!!,generalRequest.device_id!!)?.enqueue(object :
            Callback<UserModel> {
            override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                if (response.code() == 500 || response.code() == 400) {
                    val converter =
                        retrofit?.responseBodyConverter<UserModel>(
                            UserModel::class.java!!,
                            arrayOfNulls<Annotation>(0)
                        )
                    try {
                        val errorResponse = converter?.convert(response.errorBody()!!)
                        fcmTokenResponseListener.onFCMTokenResponse(errorResponse)
                    } catch (e: IOException) {
                        fcmTokenResponseListener.onFCMTokenResponseFailure("Something went wrong. Try again")
                        e.printStackTrace()
                    }

                } else if (response.code() == 200)
                    fcmTokenResponseListener.onFCMTokenResponseSuccess(response)
                else
                    fcmTokenResponseListener.onFCMTokenResponseFailure("Something went wrong. Try again")
            }

            override fun onFailure(call: Call<UserModel>, t: Throwable) {
                t.printStackTrace()
                fcmTokenResponseListener.onFCMTokenResponseFailure(getErrorMessage(t))
            }
        })
    }

}