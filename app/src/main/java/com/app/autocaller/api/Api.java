package com.app.autocaller.api;

import com.example.dtpapp.models.TokenInfoModel;
import com.example.dtpapp.models.CommandTblModel;
import com.example.dtpapp.models.DiagnosisTblModel;
import com.example.dtpapp.models.MonitoringTblModel;
import com.example.dtpapp.models.ReportTblModel;
import com.example.dtpapp.models.ReqReportTblModel;
import com.example.dtpapp.models.ReqSelfCheckModel;
import com.example.dtpapp.models.RequestCommandTblModel;
import com.example.dtpapp.models.ResSelfCheckModel;
import com.example.dtpapp.models.StatusTblModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface Api {
    @POST("/insert/token/info")
    Call<Integer> insertTokenInfo(@Body TokenInfoModel tokenInfoModel);
}
