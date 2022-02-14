package com.tulasimultispecialityhospital.connections

import com.tulasimultispecialityhospital.models.*
import retrofit2.Call
import retrofit2.http.*

interface ConnectionAPI {

    /**
     * Gets All the urls available.
     *
     * @paramResponse Callback for the response.
     */


    //get user data service
    @FormUrlEncoded
    @POST("login")
    abstract fun getLoginData(
        @Field("mobile") mobile: String?,
        @Field("password") password: String?,
        @Field("doctor_id") doctor_id: String?
    ): Call<UserModel>

    @FormUrlEncoded
    @POST("doctorprofile")
    abstract fun getDoctorProfile(@Field("doctor_id") doctor_id: String?): Call<DoctorModel>

    @FormUrlEncoded
    @POST("register")
    abstract fun postRegistrationData(
        @Field("user_fullname") user_fullname: String?,
        @Field("user_email") user_email: String?,
        @Field("user_mobile") user_mobile: String?,
        @Field("user_pwd") user_pwd: String?,
        @Field("user_gender") user_gender: String?,
        @Field("user_dob") user_dob: String?,
        @Field("user_city") user_city: String?,
        @Field("doctor_id") doctor_id: String?
    ): Call<UserModel>


    @FormUrlEncoded
    @POST("clinicslist")
    abstract fun getDoctorClinics(@Field("doctor_id") doctor_id: String?): Call<DoctorModel>

    @FormUrlEncoded
    @POST("aboutus")
    abstract fun getDoctorAbout(@Field("doctor_id") doctor_id: String?): Call<DoctorModel>

    @FormUrlEncoded
    @POST("appointments")
    abstract fun getAppointmentSlots(
        @Field("doctor_id") doctor_id: String?,
        @Field("clinic_id") clinic_id: String?,
        @Field("slot_date") slot_date: String?,
        @Field("slot_type") slot_type: String?
    ): Call<TimeSlotModel>

    @FormUrlEncoded
    @POST("book_appointment")
    abstract fun bookAppointment(
        @Field("doctor_id") doctor_id: String?,
        @Field("clinic_id") clinic_id: String?,
        @Field("slot_date") slot_date: String?,
        @Field("slot_type") slot_type: String?,
        @Field("slot_timing") slot_timing: String?,
        @Field("appointment_type") appointment_type: String?,
        @Field("user_id") user_id: String?
    ): Call<AppointmentModel>

    @FormUrlEncoded
    @POST("forgotpassword")
    abstract fun getForgotPasswordData(
        @Field("mobile") student_id: String?,
        @Field("doctor_id") doctor_id: String?
    ): Call<DoctorModel>


    @FormUrlEncoded
    @POST("user_appointments")
    abstract fun userAppointments(
        @Field("doctor_id") doctor_id: String?,
        @Field("user_id") user_id: String?
    ): Call<AppointmentModel>

    @FormUrlEncoded
    @POST("imagegallery")
    abstract fun getImagesGalleryData(@Field("doctor_id") doctor_id: String?): Call<AlbumModel>

    @FormUrlEncoded
    @POST("testimonals")
    abstract fun getTestimonialsData(@Field("doctor_id") doctor_id: String?): Call<TestimonialModel>

    @FormUrlEncoded
    @POST("deviceupdate")
    abstract fun postDeviceId(
        @Field("mobile") mobile_no: String,
        @Field("doctor_id") doctor_id: String,
        @Field("device_id") device_id: String
    ): Call<UserModel>


//    @FormUrlEncoded
//    @POST("aboutschool")
//    abstract fun getAboutSchoolData(@Field("school_id") school_id: String): Call<UserModel>
//
//    @FormUrlEncoded
//    @POST("circular")
//    abstract fun getCircularData(@Field("student_id") student_id: String): Call<CircularModel>
//
//    @FormUrlEncoded
//    @POST("dairy")
//    abstract fun getDairyData(@Field("student_id") student_id: String): Call<DairyModel>
//
//    @FormUrlEncoded
//    @POST("feeinfo")
//    abstract fun getFeeInfoData(@Field("student_id") student_id: String, @Field("school_id") school_id: String): Call<FeeModel>
//
//    @FormUrlEncoded
//    @POST("marksinfo")
//    abstract fun getMarksInfoData(@Field("student_id") student_id: String): Call<MarksModel>
//

//
//    @FormUrlEncoded
//    @POST("videogallery")
//    abstract fun getVideosGalleryData(@Field("school_id") school_id: String): Call<AlbumModel>
//
//    @FormUrlEncoded
//    @POST("switchaccount")
//    abstract fun getSwitchAccountData(
//        @Field("school_id") school_id: String,
//        @Field("currentstudent_id") currentstudent_id: String,
//        @Field("mobile_number") mobile_number: String
//    ): Call<UserModel>
//
//    @FormUrlEncoded
//    @POST("attendance")
//    abstract fun getAttendanceData(
//        @Field("student_id") student_id: String,
//        @Field("month") month: String,
//        @Field("year") year: String
//    ): Call<AttendanceModel>
//
//    @FormUrlEncoded
//    @POST("announcements")
//    abstract fun getAnnouncementData(
//        @Field("student_id") student_id: String,
//        @Field("school_id") school_id: String
//    ): Call<AnnouncementModel>
//
//    @GET
//    abstract fun downloadFileWithDynamicUrlSync(@Url fileUrl: String): Call<ResponseBody>
//
//    @FormUrlEncoded
//    @POST("raisecomplaint")
//    abstract fun postRaiseComplaintData(
//        @Field("student_id") student_id: String,
//        @Field("school_id") school_id: String,
//        @Field("message") message: String
//    ): Call<ComplaintModel>
//
//    @FormUrlEncoded
//    @POST("complaintslist")
//    abstract fun getComplaintData(@Field("student_id") student_id: String): Call<ComplaintModel>
//
//    @FormUrlEncoded
//    @POST("individualcomplaintslist")
//    abstract fun getIndividualComplaintsListData(@Field("student_id") student_id: String, @Field("raised_id") raised_id: String): Call<ComplaintIndividualModel>
//
//    @FormUrlEncoded
//    @POST("individualcomplaintresponse")
//    abstract fun postIndividualComplaintResponseData(
//        @Field("student_id") student_id: String,
//        @Field("raised_id") raised_id: String,
//        @Field("message") message: String
//    ): Call<ComplaintIndividualModel>
//
//    @FormUrlEncoded
//    @POST("studentimgupdate")
//    abstract fun postStudentImageUpdateData(@Field("student_id") student_id: String, @Field("student_image") student_image: String): Call<UserModel>
//
//    @FormUrlEncoded
//    @POST("circularmessage")
//    abstract fun sendCircularReadStatusData(@Field("student_id") student_id: String, @Field("message_id") message_id: String): Call<CircularModel>
//
//    @FormUrlEncoded
//    @POST("attendanceallmonths")
//    abstract fun getAttendanceAllMonthsData(@Field("student_id") student_id: String): Call<AttendanceModel>
//

}